import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ResetEmailRequest } from '../../../services/models';
import { Api } from '../../../services/api';
import { resetEmail } from '../../../services/functions';
import { ToastrService } from 'ngx-toastr';

@Component({
  imports: [],
  selector: 'app-reset-email',
  templateUrl: './reset-email.component.html',
})
export class ResetEmailComponent implements OnInit{

  isLoading:boolean = true;

  rawToken:string | null = null;
  newEmail:string | null = null;

  constructor(
     private activatedRoute:ActivatedRoute,
     private api:Api,
     private toastr:ToastrService,
     private router:Router
  ){}

  ngOnInit(): void {
    this.rawToken = this.activatedRoute.snapshot.queryParamMap.get('token');
    this.newEmail = this.activatedRoute.snapshot.queryParamMap.get('email');
    if(this.rawToken && this.newEmail){
      this.resetEmail({'newEmail': this.newEmail,'rawToken': this.rawToken})
    }
  }

  //reset-email
  resetEmail(request:ResetEmailRequest){
    this.api.invoke(resetEmail,{
      body: request
    }).then(()=>{
      this.toastr.success("Email Updated Successfully");
    }).catch((err)=>{
      console.log(err)
      if(err.error.message){
        this.toastr.error(err.error.message);
      } else {
        this.toastr.error("Request Failed, Please Try Again")
      }
    }).finally(()=>{
      this.isLoading = false;
      this.router.navigate(['/admin']);
    })
  }
}
