import { Component, OnInit } from '@angular/core';
import { ButtonComponent } from "../../ui/button/button.component";
import { InputFieldComponent } from "../../form/input/input-field.component";
import { LabelComponent } from "../../form/label/label.component";
import { Api } from '../../../../services/api';
import { LoginService } from '../../../services/login.service';
import { changePassword } from '../../../../services/functions';
import { ChangePasswordRequest } from '../../../../services/models';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { AuthService } from '../../../../services/auth-service/auth.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

import {PasswordMatchValidatorService} from '../../../services/password-match-validator.service';

@Component({
  selector: 'app-change-password-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-password-form.component.html',
  styles: ``,
})
export class ChangePasswordFormComponent implements OnInit{
  
  
  isAuthenticated = false;

  isProcessing:boolean = false;

  showPassword:boolean = false;
  showNewPassword:boolean= false;
  showConfirmPassword:boolean = false;

  requestForm:FormGroup;

  constructor(
    private api: Api,
    private loginService: LoginService,
    private toastr: ToastrService,
    private router: Router,
    private authService: AuthService,
    private fb:FormBuilder,
  ) {
    this.requestForm = this.fb.group({
      'email':['',[Validators.required]],
      'currentPassword':['',[Validators.required]],
      'newPassword':['', [Validators.required]],
      'confirmPassword':['',[Validators.required]]
    },{
      validators: PasswordMatchValidatorService('newPassword','confirmPassword')
    })
  }

  ngOnInit(): void {
    let email = this.loginService.currentEmail;
    this.isAuthenticated = this.authService.isAuthenticated;
    if(this.isAuthenticated){
      email = this.authService.profile?.email as string;
    }
    this.requestForm.patchValue({email: email})

  }

   onChangePassword() {  
    if(this.requestForm.valid){
      const request:ChangePasswordRequest = this.requestForm.value;
      this.isProcessing = true;
      this.changePassword(request);
    } else{
      this.requestForm.markAllAsTouched();
    }
  }

  //change password
  changePassword(request:ChangePasswordRequest){
    this.api.invoke(changePassword, { 
      body: request 
    }).then(() => {

      this.toastr.success('Password changed successfully');

      if(this.isAuthenticated){
        this.router.navigate(['/']);
      }else{
        this.router.navigate(['/signin']);
      }
  }).catch((err) => {
      if(err.error)  {
        this.toastr.error(err.error.message);
      }  else {
        this.toastr.error('Failed to change password');
      }   
  }).finally(()=>{
    this.isProcessing = false;
  });
  }

  onCancel(){
      this.router.navigate(['/signin']) 
  }
 

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  toggleNewPasswordVisibility() {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  isInvalid(controlName: string): boolean {
    const control = this.requestForm.get(controlName);
    return !!control && control.invalid && control.touched;
  } 

  get isMismatch():boolean{
    return this.requestForm.hasError('passwordMismatch');
   }
}
