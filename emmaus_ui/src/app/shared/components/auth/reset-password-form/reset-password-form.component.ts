import { Component, ElementRef, Input, input, OnInit, QueryList, ViewChildren } from '@angular/core';
import { LabelComponent } from "../../form/label/label.component";
import { InputFieldComponent } from "../../form/input/input-field.component";
import { ButtonComponent } from "../../ui/button/button.component";
import { Router } from '@angular/router';
import { Api } from '../../../../services/api';
import { ToastrService } from 'ngx-toastr';
import { resetPassword } from '../../../../services/functions';
import { ResetPasswordRequest } from '../../../../services/models';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {PasswordMatchValidatorService} from '../../../services/password-match-validator.service';

@Component({
  selector: 'app-reset-password-form',
  imports: [LabelComponent, ButtonComponent, ReactiveFormsModule, CommonModule],
  templateUrl: './reset-password-form.component.html',
  styles: ``,
})
export class ResetPasswordFormComponent implements OnInit {
  
   
  @ViewChildren('otpInput') inputs!: QueryList<ElementRef>;
  
  @Input() rawToken:string | null = null; 

  showPassword:boolean = false;
  showConfirmPassword:boolean = false;


  requestForm:FormGroup;

  constructor(
    private router: Router,
    private api: Api,
    private toastr: ToastrService,
    private fb:FormBuilder
  ){
    this.requestForm = this.fb.group({
      'rawToken':['',[Validators.required]],
      'newPassword':['',[Validators.required]],
      'confirmPassword':['',[Validators.required]]
    },{
      validators: PasswordMatchValidatorService('newPassword','confirmPassword')
    })
  }

  ngOnInit() {  
    if(this.rawToken){
      this.requestForm.patchValue({rawToken: this.rawToken})
    }
  }

  onResetPassword(){
    if(this.requestForm.valid){
      const request:ResetPasswordRequest = this.requestForm.value;
      this.resetPassword(request);
    } else {
      this.requestForm.markAllAsTouched();
    }
  }

  resetPassword(request:ResetPasswordRequest){
    this.api.invoke(resetPassword, { 
      body: request
    }).then(() => {
      this.toastr.success('Password reset successfully');
      this.router.navigate(['/signin']);
    }).catch((err) => {
      console.log(err);
      if(err.error){
        this.toastr.error(err.error.message)
      } else{
        this.toastr.error("Request Failed");
      }
    });
  }

  togglePasswordVisibility(){
    this.showPassword = !this.showPassword;
  }


  toggleConfirmPasswordVisibility(){
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
