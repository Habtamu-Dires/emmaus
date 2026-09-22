import { Component } from '@angular/core';
import { AuthPageLayoutComponent } from "../../../shared/layout/auth-page-layout/auth-page-layout.component";
import { ChangePasswordFormComponent } from "../../../shared/components/auth/change-password-form/change-password-form.component";

@Component({
  selector: 'app-change-temp-password',
  imports: [AuthPageLayoutComponent, ChangePasswordFormComponent],
  templateUrl: './change-temp-password.component.html',
  styles: ``,
})
export class ChangeTempPasswordComponent {
  
}
