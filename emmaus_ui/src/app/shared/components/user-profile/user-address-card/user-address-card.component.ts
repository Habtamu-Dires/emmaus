import { Component } from '@angular/core';
import { ModalService } from '../../../services/modal.service';

import { InputFieldComponent } from '../../form/input/input-field.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { LabelComponent } from '../../form/label/label.component';
import { ModalComponent } from '../../ui/modal/modal.component';
import { FormsModule } from '@angular/forms';
import { EmailUpdateRequest, UserProfile } from '../../../../services/models';
import { updateEmail, updateUserProfile } from '../../../../services/functions';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Api } from '../../../../services/api';

@Component({
  selector: 'app-user-address-card',
  imports: [
    InputFieldComponent,
    ButtonComponent,
    LabelComponent,
    ModalComponent,
    FormsModule,
  ],
  templateUrl: './user-address-card.component.html',
  styles: ``
})
export class UserAddressCardComponent {

  user:UserProfile;
  isOpen = false;
  email:string | undefined;

  constructor(
    public modal: ModalService,
    private authService: AuthService,
    private api:Api,
    private toastr:ToastrService,
  ) {
    this.user = this.authService.profile as UserProfile;
    this.email = this.user.email;
  }

  
  openModal() { this.isOpen = true; }
  closeModal() { this.isOpen = false; }


  handleSave() {
    if(this.email !== this.user.email){
      const newEmail = this.user.email;
      const request:EmailUpdateRequest = {email: newEmail}
      this.updateEmail(request);
    }
    this.isOpen = false;
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
}
