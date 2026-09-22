import { Component } from '@angular/core';
import { LabelComponent } from "../../form/label/label.component";
import { ButtonComponent } from "../../ui/button/button.component";
import { Router } from "@angular/router";
import { Api } from '../../../../services/api';
import { ToastrService } from 'ngx-toastr';
import { LoginService } from '../../../services/login.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ForgetPasswordRequest } from '../../../../services/models';
import { forgetPassword } from '../../../../services/functions';

@Component({
  selector: 'app-forget-password-form',
  imports: [LabelComponent, ReactiveFormsModule, CommonModule],
  templateUrl: './forget-password-form.component.html',
  styles: ``,
})
export class ForgetPasswordFormComponent {

  isProcessing:boolean = false;

  requestForm:FormGroup;

  constructor(
    private router:Router,
    private api:Api,
    private toastr: ToastrService,
    private loginService: LoginService,
    private fb:FormBuilder
  ){
    this.requestForm = this.fb.group({
      'email': ['',[Validators.required,Validators.email]]
    })
  }

  async onReset() {
    if(this.requestForm.valid){
      this.isProcessing = true;
      const request:ForgetPasswordRequest = this.requestForm.value;
      this.sendForgetPasswordRequest(request);
    } else {
      this.requestForm.markAllAsTouched();
    }
  }

  sendForgetPasswordRequest(request:ForgetPasswordRequest){
    this.api.invoke(forgetPassword,{
      body: request
    }).then((res)=>{
        this.toastr.success("Please check your " + request.email +  " for reset");
        this.router.navigate(['/signin']);
    })
    .catch((err) => {
        if(err.error.message){
          this.toastr.error(err.error.message);
        } else{
          this.toastr.error("Request Failed");
        }
      }).finally(() => {
        this.isProcessing = false;
      });
  }

  backToLogin(){
    this.router.navigate(['signin']);
  }

  isInvalid(controlName: string): boolean {
    const control = this.requestForm.get(controlName);
    return !!control && control.invalid && control.touched;
  } 
}
