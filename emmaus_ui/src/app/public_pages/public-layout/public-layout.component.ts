import { Component } from '@angular/core';

import { RouterOutlet } from '@angular/router';
import { PublicHeaderComponent } from '../../shared/public_components/public-header/public-header.component';
import { PublicFooterComponent } from '../../shared/public_components/public-footer/public-footer.component';

@Component({
  selector: 'app-public-layout',
  standalone: true,
  imports: [RouterOutlet, PublicHeaderComponent, PublicFooterComponent],
  templateUrl: './public-layout.component.html',
})
export class PublicLayoutComponent {}