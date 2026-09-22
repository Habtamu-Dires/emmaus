
import { Component } from '@angular/core';
import { LabelComponent } from '../../form/label/label.component';
import { ButtonComponent } from '../../ui/button/button.component';
import {  Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Api } from '../../../../services/api';
import { login } from '../../../../services/functions';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { LoginService } from '../../../services/login.service';
import { AuthenticationRequest, AuthTokens } from '../../../../services/models';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-signin-form',
  imports: [
    LabelComponent,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule
],
  templateUrl: './signin-form.component.html',
  styles: ``
})
export class SigninFormComponent {

  showPassword = false;
  isProcessing:boolean = false;

  signInForm:FormGroup;

  constructor(
    private api:Api,
    private router: Router,
    private toastr: ToastrService,
    private authService: AuthService,
    private loginService: LoginService,
    private fb:FormBuilder
  ) {
    this.signInForm = this.fb.group({
      'email':['',[Validators.required, Validators.email]],
      'password':['',[Validators.required]]
    })
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSignIn() {
   if(this.signInForm.valid){
    this.isProcessing = true;
    const request : AuthenticationRequest = this.signInForm.value;
    this.login(request);
   } else {
    this.signInForm.markAllAsTouched();
   }
  }

  // login
  async login(request:AuthenticationRequest) {
      try {
        const res:AuthTokens = await this.api.invoke(login, { body: request });
        if(res.isTempPassword){
          this.loginService.setEmail(request.email);
          this.router.navigate(['/change-temp-password']);
          return;
        }

        if(res.accessToken && res.refreshToken) {
          await this.authService.login(res.accessToken, res.refreshToken);
        } else {
          this.toastr.error('Invalid response from server', 'Login Failed');
        }
      } catch (err: any) {
        console.log("any error ???")
        this.isProcessing = false;
        if (err.error) {
          if(err?.error.code === 'ERR_NOT_VERIFIED'){ 
            this.toastr.info(err.error?.message, 'Verification Required');
            this.loginService.setEmail(request.email);
            this.router.navigate(['/otp']);
          } else {
            this.toastr.error(err.error?.message, 'Login Failed');
          }

        } else {
          console.error('An unexpected error occurred:', err);
          this.toastr.error('Login Failed');
        }
       } finally {
        this.isProcessing = false;
       }
     }

  isInvalid(controlName: string): boolean {
    const control = this.signInForm.get(controlName);
    return !!control && control.invalid && control.touched;
  } 
 }

