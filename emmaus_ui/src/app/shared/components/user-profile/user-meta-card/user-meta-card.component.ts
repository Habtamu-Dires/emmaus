import { Component } from '@angular/core';
import { ModalService } from '../../../services/modal.service';
import { InputFieldComponent } from '../../form/input/input-field.component';

import { ModalComponent } from '../../ui/modal/modal.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { FormsModule } from '@angular/forms';
import { UserProfile } from '../../../../services/models';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Api } from '../../../../services/api';
import { updateUserProfile } from '../../../../services/functions';

@Component({
  selector: 'app-user-meta-card',
  imports: [
    ModalComponent,
    InputFieldComponent,
    ButtonComponent,
    FormsModule,
  ],
  templateUrl: './user-meta-card.component.html',
  styles: ``
})
export class UserMetaCardComponent {

  user: UserProfile;

  isOpen = false;

  constructor(
    public modal: ModalService,
    private authService: AuthService,
    private api:Api,
    private toastr:ToastrService
  ) {
    this.user = this.authService.profile as UserProfile;
  }

  
  openModal() { this.isOpen = true; }
  closeModal() { this.isOpen = false; }  

  handleSave() {
    this.updateProfile();
    this.isOpen = false;
  }

  updateProfile(){
    this.api.invoke(updateUserProfile,{body:this.user}).then(res=>{
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
}
