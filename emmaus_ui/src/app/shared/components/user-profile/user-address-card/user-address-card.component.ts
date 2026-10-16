import { Component } from '@angular/core';
import { ModalService } from '../../../services/modal.service';

import { InputFieldComponent } from '../../form/input/input-field.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { LabelComponent } from '../../form/label/label.component';
import { ModalComponent } from '../../ui/modal/modal.component';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { EmailUpdateRequest, UserProfile } from '../../../../services/models';
import { updateEmail } from '../../../../services/functions';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Api } from '../../../../services/api';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-address-card',
  imports: [
    ButtonComponent,
    LabelComponent,
    ModalComponent,
    FormsModule,
    ReactiveFormsModule,
    CommonModule
  ],
  templateUrl: './user-address-card.component.html',
  styles: ``
})
export class UserAddressCardComponent {

  user:UserProfile;
  isOpen = false;

  emailForm:FormGroup;

  constructor(
    public modal: ModalService,
    private authService: AuthService,
    private api:Api,
    private toastr:ToastrService,
    private fb:FormBuilder
  ) {
    this.user = this.authService.profile as UserProfile;

    this.emailForm = this.fb.group({
      email:[this.user.email,[Validators.required,Validators.email]]
    })
  }

  
  openModal() { this.isOpen = true; }
  closeModal() { this.isOpen = false; }


  handleSave() {
    if(this.emailForm.valid){
      const email = this.emailForm.get('email')?.value;

      if(email !== this.user.email){
        const request:EmailUpdateRequest = {email: email}
        this.updateEmail(request);
      }
      this.isOpen = false;
    } else {
      this.emailForm.markAllAsTouched();
    }
    
  }

  updateEmail(request:EmailUpdateRequest){
    this.api.invoke(updateEmail,{
      body: request
    }).then(()=>{
      this.toastr.success("Reset Email Sent");
    }).catch((err)=>{
      if(err.error){
        this.toastr.error(err.error.message);
      } else {
        this.toastr.error("Request Failed");
      }
    }).finally(()=>{
      this.isOpen = false;
    })
  }

  isInvalid(controlName: string): boolean {
    const control = this.emailForm.get(controlName);
    return !!control && control.invalid && control.touched;
  }
}
