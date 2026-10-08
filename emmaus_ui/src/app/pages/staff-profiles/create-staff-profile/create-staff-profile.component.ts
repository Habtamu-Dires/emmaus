import { Component, OnInit } from '@angular/core';
import { ButtonComponent } from '../../../shared/components/ui/button/button.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Api } from '../../../services/api';
import { ToastrService } from 'ngx-toastr';
import { CreatStaffProfileRequest, StaffProfileResponse } from '../../../services/models';
import { createStaffProfile, updateProfile, updateUserProfile } from '../../../services/functions';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LabelComponent } from '../../../shared/components/form/label/label.component';
import { FileUploaderComponent } from '../../../shared/components/file-uploader/file-uploader.component';
import { SharedService } from '../../../shared/services/shared.service';

@Component({
  imports: [ButtonComponent, ReactiveFormsModule, CommonModule, LabelComponent, FileUploaderComponent],
  selector: 'app-create-staff-profile',
  templateUrl: './create-staff-profile.component.html',
})
export class CreateStaffProfileComponent implements OnInit{


  staffForm: FormGroup;
  isLoading:boolean = false;
  isEditMode:string | undefined = undefined;
  publicId:string | undefined = undefined;

  constructor(
    private api: Api,
    private toastr: ToastrService,
    private router:Router,
    private fb: FormBuilder,
    private activatedRoute:ActivatedRoute,
    private sharedService:SharedService
  ) {
    this.staffForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      position: ['', Validators.required],
      department: [''],
      description: ['', Validators.required],
      bio: [''],
      linkedinUrl: [''],
      profilePic: [''],
      displayOrder: [99, Validators.required],
    });
  }

  ngOnInit(): void {
    this.isEditMode = this.activatedRoute.snapshot.queryParamMap.get('edit') || undefined;
    if(this.isEditMode){
     const profile:StaffProfileResponse = this.sharedService.selectedStaffProfile;
     this.publicId = profile.publicId;

     this.staffForm.patchValue({
      firstName: profile.firstName,
      lastName: profile.lastName,
      email: profile.email,
      phone: profile.phone,
      position: profile.position,
      department: profile.department,
      description: profile.description,
      bio: profile.bio,
      linkedinUrl: profile.linkedinUrl,
      profilePic: profile.profilePic,
      displayOrder: profile.displayOrder
    });
    }
  }

  onCreateNewStaff() {
    if (this.staffForm.invalid) {
      this.staffForm.markAllAsTouched();
      return;
    }
    const request: CreatStaffProfileRequest = this.staffForm.value;
    if(this.isEditMode){
      this.updateStaffProfile(request);
    } else {
      this.createNewStaff(request);
    }
  }

  createNewStaff(request: CreatStaffProfileRequest) {
    this.isLoading = true;
    this.api
      .invoke(createStaffProfile, { body: request })
      .then(() => {
        this.toastr.success('New Staff Profile Created');
        this.backToStaffProfile();
      })
      .catch((err) => {
        console.error(err);
        this.toastr.error(err?.error?.message || 'Request Failed');
      })
      .finally(() => {
        this.isLoading = false;
      });
  }

  updateStaffProfile(request: CreatStaffProfileRequest) {
    this.isLoading = true;
    this.api
      .invoke(updateProfile, {
         body: request ,
        'public-id': this.publicId as string
        })
      .then(() => {
        this.toastr.success('Staff Profile Updated');
        this.backToStaffProfile();
      })
      .catch((err) => {
        console.error(err);
        this.toastr.error(err?.error?.message || 'Request Failed');
      })
      .finally(() => {
        this.isLoading = false;
      });
  }

  onProfilePicUploaded(url:string){
    this.staffForm.patchValue({ profilePic: url });
  }

  // ─── Helpers ─────────────────────────────────────────────────
  isInvalid(controlName: string): boolean {
    const control = this.staffForm.get(controlName);
    return !!control && control.invalid && control.touched;
  }

  backToStaffProfile(){
    this.router.navigate(['/admin','staff-profile']);
  }
}
