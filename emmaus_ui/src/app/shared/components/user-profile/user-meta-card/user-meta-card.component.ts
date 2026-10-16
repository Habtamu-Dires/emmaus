import { Component } from '@angular/core';
import { ModalService } from '../../../services/modal.service';
import { InputFieldComponent } from '../../form/input/input-field.component';

import { ModalComponent } from '../../ui/modal/modal.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { UpdateUserProfileRequest, UserProfile } from '../../../../services/models';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Api } from '../../../../services/api';
import { updateUserProfile } from '../../../../services/functions';
import { CommonModule } from '@angular/common';
import { LabelComponent } from '../../form/label/label.component';

@Component({
  selector: 'app-user-meta-card',
  imports: [
    ModalComponent,
    ButtonComponent,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    LabelComponent
],
  templateUrl: './user-meta-card.component.html',
  styles: ``
})
export class UserMetaCardComponent {

  user: UserProfile;

  isOpen = false;

  userProfileForm:FormGroup;

  constructor(
    public modal: ModalService,
    private authService: AuthService,
    private api:Api,
    private toastr:ToastrService,
    private fb:FormBuilder
  ) {
    this.user = this.authService.profile as UserProfile;

    this.userProfileForm = this.fb.group({
      firstName: [this.user.firstName, [Validators.required]],
      lastName: [this.user.lastName, [Validators.required]],
      profilePic: [this.user.profilePic]
    })
  }

  
  openModal() { this.isOpen = true; }
  closeModal() { this.isOpen = false; }  

  handleSave() {
    if(this.userProfileForm.valid){
      const request:UpdateUserProfileRequest = this.userProfileForm.value;
      this.updateProfile(this.user.publicId,request);
    } else {
      this.userProfileForm.markAllAsTouched();
    }
    
  }

  updateProfile(publicId:string, request:UpdateUserProfileRequest){
    this.api.invoke(updateUserProfile,{
      'public-id':publicId,
      body:request
    }).then(res=>{
      this.user = res as UserProfile;
      this.toastr.success('Profile updated successfully');
      this.closeModal();
    }).catch(err=>{
      if(err.error){
        this.toastr.error(err.error.message);
      } else {
        console.log(err);
        this.toastr.error('An unexpected error occurred');
      }
    }).finally(()=>{
      this.isOpen = false;
    })
  }

  isInvalid(controlName: string): boolean {
    const control = this.userProfileForm.get(controlName);
    return !!control && control.invalid && control.touched;
  }
}
