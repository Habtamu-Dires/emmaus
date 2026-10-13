import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ButtonComponent } from '../../shared/components/ui/button/button.component';
import { BadgeComponent } from '../../shared/components/ui/badge/badge.component';
import { LabelComponent } from '../../shared/components/form/label/label.component';
import { ModalComponent } from '../../shared/components/ui/modal/modal.component';
import { CreateUserRequest, PageResponseUserProfile, UserProfile } from '../../services/models';
import { Api } from '../../services/api';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../services/auth-service/auth.service';
import { activateUser, createUser, deleteUserIdentity, disableUser, getPagesOfUser } from '../../services/functions';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

export type SearchType = 'name' | 'phone';

@Component({
  selector: 'app-admins-table',
  standalone: true,
  imports: [
    CommonModule,
    ButtonComponent,
    BadgeComponent,
    LabelComponent,
    ModalComponent,
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './admins-table.component.html',
})
export class AdminsTableComponent implements OnInit {
  remark = new FormControl('', [Validators.required, Validators.minLength(3)]);

  isSuperAdmin = false;
  admins: UserProfile[] = [];

  currentPage = 1;
  totalPages = 1;
  pageSize = 10;
  totalElements = 0;
  isLoading = false;

  currentStatus = '';
  searchType: SearchType = 'name';
  searchTerm = '';

  selectedAdmin: UserProfile | undefined;
  createAdminForm: FormGroup;

  isCreateModalOpen = false;
  isDetailModalOpen = false;

  currentUser:UserProfile | undefined;

  private phoneRegex = /^(09|07)\d{8}$/;

  constructor(
    private api: Api,
    private toastr: ToastrService,
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder,
    private dialog:MatDialog,
    private cdr: ChangeDetectorRef
  ) {
    this.createAdminForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(this.phoneRegex)]],
      role: ['ADMIN', Validators.required],
      remark: [''],
    });
  }

  ngOnInit(): void {
    this.isSuperAdmin = this.authService.isSuperAdmin;
    this.currentUser = this.authService.profile;
    if (this.isSuperAdmin) {
      this.loadAdmins();
    }
  }

  onSearch() {
    this.loadAdmins(1);
  }

  onFilterChange() {
    this.loadAdmins(1);
  }

  loadAdmins(page: number = 1) {
    this.isLoading = true;
    const params: Record<string, unknown> = {
      page: page - 1,
      size: this.pageSize,
    };
    if (this.currentStatus) params['status'] = this.currentStatus;
    if (this.searchTerm?.trim()) {
      params[this.searchType] = this.searchTerm.trim();
    }
    this.getPageOfAdmins(params);
  }

  getPageOfAdmins(params: Record<string, unknown>) {
    this.api
      .invoke(getPagesOfUser, params)
      .then((res: PageResponseUserProfile) => {
        if (res.content) {
          this.admins = res.content;
          this.currentPage = Number(res.number ?? 0) + 1;
          this.totalPages = res.totalPages ?? 1;
          this.totalElements = res.totalElements ?? 0;
        }
      })
      .catch((err) => {
        this.toastr.error(err?.error?.message || 'Failed to load admins');
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      });
  }

  deleteUserIdentity(userId:string){
    this.api.invoke(deleteUserIdentity,{
      'user-id': userId
    })
    .then(()=>{
      this.toastr.success("User Deleted");
      this.loadAdmins(1);
    }).catch((err) => {
        this.toastr.error(err?.error?.message || 'Failed to load admins');
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
    });
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.loadAdmins(page);
  }

  openCreateAdminModal() {
    this.createAdminForm.reset({ role: 'ADMIN' });
    this.isDetailModalOpen = false;
    this.isCreateModalOpen = true;
  }

  closeCreateModal() {
    this.isCreateModalOpen = false;
    this.createAdminForm.reset({ role: 'ADMIN' });
  }

  handleCreateAdmin() {
    if (this.createAdminForm.invalid) {
      this.createAdminForm.markAllAsTouched();
      return;
    }
    const request: CreateUserRequest = this.createAdminForm.value;
    this.createAdmin(request);
  }

  createAdmin(payload: CreateUserRequest) {
    this.api
      .invoke(createUser, { body: payload })
      .then(() => {
        this.toastr.success('Admin created');
        this.closeCreateModal();
        this.loadAdmins(1);
      })
      .catch((err) => {
        this.toastr.error(err?.error?.message || 'Failed to create admin');
      });
  }

  openDetailModal(admin: UserProfile) {
    this.selectedAdmin = admin;
    this.remark.reset();
    this.isCreateModalOpen = false;
    this.isDetailModalOpen = true;
  }

  closeDetailModal() {
    this.isDetailModalOpen = false;
    this.selectedAdmin = undefined;
    this.remark.reset();
  }

  activateUser(adminId: string) {
    if (!adminId) return;
    this.api
      .invoke(activateUser, { 'public-id': adminId })
      .then(() => {
        this.toastr.success('Status successfully updated');
        this.closeDetailModal();
        this.loadAdmins(this.currentPage);
      })
      .catch((err) => {
        this.toastr.error(err?.error?.message || 'Request failed');
      });
  }

  disableUser(adminId: string) {
    if (!adminId) return;
    if (this.remark.invalid) {
      this.remark.markAsTouched();
      this.toastr.error('Remark is mandatory (min 3 characters)');
      return;
    }
    this.api
      .invoke(disableUser, {
        'public-id': adminId,
        body: { text: this.remark.value as string },
      })
      .then(() => {
        this.toastr.success('Status successfully updated');
        this.closeDetailModal();
        this.loadAdmins(this.currentPage);
      })
      .catch((err) => {
        this.toastr.error(err?.error?.message || 'Request failed');
      });
  }

  getBadgeColor(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'success';
      case 'DISABLED':
        return 'error';
      default:
        return 'warning';
    }
  }

  isInvalid(controlName: string): boolean {
    const control = this.createAdminForm.get(controlName);
    return !!control && control.invalid && control.touched;
  }

  /** Roles may be string | string[] depending on API */
  rolesList(admin: UserProfile): string[] {
    const roles = (admin as any).roles;
    if (Array.isArray(roles)) return roles;
    if (typeof roles === 'string') return [roles];
    return [];
  }

  backToHomePage() {
    this.router.navigate(['/']);
  }

  onDelete(publicId:any, name:any){
        if(publicId){
          const dialog = this.dialog.open(ConfirmDialogComponent,{
              width: '400px',
              data:{
                message:`you wants to delete ${name} admin`,
                buttonName: 'Delete',
                isWarning: true
              }
          });
          dialog.afterClosed().subscribe((result) => {
            if(result){
              this.deleteUserIdentity(publicId)
            }
          });
        }
    }
}