import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Api } from '../api';
import {jwtDecode} from 'jwt-decode';
import { getUserProfile, logout, refresh } from '../functions';
import { UserProfile } from '../models';
import { ToastrService } from 'ngx-toastr';
import { Location } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private _token: string | null = null;
  private _refreshToken: string | null = null;
  private _profile: UserProfile | undefined;
  private _decodedToken: any | null = null;
  private _status: 'OTP_SENT' | 'VERIFIED' | 'PROFILE_CREATED' | 'PENDING' | 'APPROVED' | 'ACTIVE' | 'DISABLED' | 'SUSPENDED' | 'REJECTED' | undefined;
  private startTokenRefreshInterval?: ReturnType<typeof setInterval>;

  private refreshInProgress: Promise<boolean> | null = null;

  private logoutInProgress = false;

  constructor(
    private http: HttpClient,
    private router: Router,
    private api: Api,
    private toastr: ToastrService,
    private location: Location
  ) {}

  get token() {
    return this._token;
  }

  get profile(): UserProfile | undefined {
    return this._profile;
  }

  async init() {
    this.setAccessToken(localStorage.getItem('access_token'));
    this._refreshToken = localStorage.getItem('refresh_token');

    if (this._token && this._refreshToken) {
      if (this.isTokenExpired()) {

        const refreshed = await this.refreshToken();

        if (!refreshed) {
          this.logout();
          return;
        }
      }

      await this.loadProfile();

      
      this.startTokenRefresh();

      const currentUrl = this.location.path();
      if (!currentUrl.startsWith('/reset-email')) {
         await this.router.navigate(['/admin']);
      }
    } 
  }

  // set access token and decode it
  private setAccessToken(token: string | null) {
    this._token = token;
    this._decodedToken = token ? jwtDecode(token) : null;
  }

  // login 
  async login(accessToken: string, refreshToken: string) {
    this.setAccessToken(accessToken);
    if(this.isAdmin || this.isSuperAdmin || this.isConsignor) {
      this.handleLoginSuccess({ access_token: accessToken, refresh_token: refreshToken });
    } else {
      this.toastr.error('Access Denied');
      this.logout();
    }
  }
  

  // Store tokens after successful verifyOtp (call this in LoginComponent after success)
  async handleLoginSuccess(res: { access_token: string; refresh_token: string }) {
    this._refreshToken = res.refresh_token;
    localStorage.setItem('access_token', this._token ?? '');
    localStorage.setItem('refresh_token', this._refreshToken);
    await this.loadProfile();
    this.startTokenRefresh();
    this.router.navigate(['/admin']);
  }

  // Logout
  async logout() {

    console.log('Logging out...');
    if (this.logoutInProgress) {
      return;
    }

    console.log('Logout in progress...');

    this.logoutInProgress = true;

    const refreshToken = this._refreshToken;

    // Revoke refresh token in background
    if (refreshToken) {
      this.api.invoke(logout, {
        body: { refreshToken: refreshToken }
      }).then(()=>{
        this.logoutInProgress = false;
      })
      .catch(err => {
        console.error('Logout request failed:', err);
        this.logoutInProgress = false;
      });
    }

    // Clear local authentication state immediately
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');

    this._token = null;
    this._refreshToken = null;
    this._profile = undefined;

    this.stopTokenRefresh();

    this.router.navigate(['/signin']);

}

  

  // Is authenticated
  get isAuthenticated(): boolean {
    return !!this._token && !this.isTokenExpired();
  }

  // Check if token is expired (with optional offset in seconds)
  private isTokenExpired(offset: number = 0): boolean {
    if (!this._decodedToken) return true;
    return this._decodedToken.exp < (Date.now() / 1000 + offset);
  }

  // Get roles
  get roles(): string[]  {
    return this._decodedToken?.roles || [];
  }

  // Check if ADMIN
  get isAdmin(): boolean {
    return this.roles.includes('ADMIN');
  }

  // Check if SUPER_ADMIN
  get isSuperAdmin(): boolean {
    return this.roles.includes('SUPER_ADMIN');
  }

  // Check if CONSIGNOR
  get isConsignor(){
    return this.roles.includes('CONSIGNOR');
  }

  get status(){
    return this._status;
  }

  updateStatus(status:'OTP_SENT' | 'VERIFIED' | 'PROFILE_CREATED' | 'PENDING' | 'APPROVED' | 'ACTIVE' | 'DISABLED' | 'SUSPENDED' | 'REJECTED'){
    this._status = status;
  }

  // Get the current access token
  getToken(): string | null {
    return this._token;
  }

  // Load user profile from backend
  private async loadProfile() {
    if (this._profile) return;
    try {
      const profile:UserProfile = await this.api.invoke(getUserProfile);
      this._profile = profile;
      this.updateStatus(this._profile.status);
    } catch (err:any) {
      if(err instanceof HttpErrorResponse)  {
        try {
          const error = JSON.parse(err.error);
          this.toastr.error(error.message);
        } catch {
          this.toastr.error('Failed to load user profile');
        }
      }
      this.logout();
    }
  }

  // Start proactive token refresh
  startTokenRefresh() {
    if (this.startTokenRefreshInterval) {
      clearInterval(this.startTokenRefreshInterval);
    }

    this.startTokenRefreshInterval = setInterval(async () => {
      const minValidity = 120;

      if (!this.isTokenExpired(minValidity)) {
        return;
      }

      const refreshed = await this.refreshToken();

      if (!refreshed) {
        console.error('Proactive token refresh failed');

        await this.logout();
      }
    }, 60000);
}

  stopTokenRefresh() {
    if (this.startTokenRefreshInterval) {
      clearInterval(this.startTokenRefreshInterval);
    }
  }

  // Reactive/proactive token refresh
  async refreshToken(minValidity: number = -1): Promise<boolean> {
    if (!this._refreshToken) {
      return false;
    }

    // If another refresh is already running, wait for it
    if (this.refreshInProgress) {
      return this.refreshInProgress;
    }

    this.refreshInProgress = this.performRefresh();

    try {
      return await this.refreshInProgress;
    } finally {
      this.refreshInProgress = null;
    }
  }

  private async performRefresh(): Promise<boolean> {
      const refreshToken = this._refreshToken;

      if (!refreshToken) {
        return false;
      }

      try {
        const res = await this.api.invoke(refresh, {
          body: { refreshToken }
        });

        this.setAccessToken(res.accessToken ?? '');

        if (res.refreshToken) {
          this._refreshToken = res.refreshToken;
          localStorage.setItem('refresh_token', this._refreshToken);
        }

        localStorage.setItem('access_token', this._token ?? '');

        return true;

      } catch (error) {
        console.error('Token refresh failed:', error);

        return false;
      }
  }

  get hasRefreshToken(): boolean {
    return !!this._refreshToken;
  }
}