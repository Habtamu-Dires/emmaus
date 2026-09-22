import { Component, OnInit } from '@angular/core';
import { AuthPageLayoutComponent } from "../../../shared/layout/auth-page-layout/auth-page-layout.component";
import { ResetPasswordFormComponent } from "../../../shared/components/auth/reset-password-form/reset-password-form.component";
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-reset-password',
  imports: [AuthPageLayoutComponent, ResetPasswordFormComponent],
  templateUrl: './reset-password.component.html',
  styles: ``,
})
export class ResetPasswordComponent implements OnInit{

  rawToken:string | null = null;

  constructor(
    private activatedRoute:ActivatedRoute,
  ){}

  ngOnInit(): void {
     this.rawToken = this.activatedRoute.snapshot.queryParamMap.get('token');
  }
}
