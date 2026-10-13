import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-public-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './public-footer.component.html',
})
export class PublicFooterComponent {
  readonly currentYear = new Date().getFullYear();

  constructor(private router:Router) {}

  goToContactUs() {
    this.router.navigate(['/contact']);
  }
}
