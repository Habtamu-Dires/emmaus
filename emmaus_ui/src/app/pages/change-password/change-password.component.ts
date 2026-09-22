import { Component } from '@angular/core';
import { PageBreadcrumbComponent } from "../../shared/components/common/page-breadcrumb/page-breadcrumb.component";
import { ChangePasswordFormComponent } from "../../shared/components/auth/change-password-form/change-password-form.component";

@Component({
  selector: 'app-change-password',
  imports: [PageBreadcrumbComponent, ChangePasswordFormComponent],
  templateUrl: './change-password.component.html',
  styles: ``
})
export class ChangePasswordComponent {

}
