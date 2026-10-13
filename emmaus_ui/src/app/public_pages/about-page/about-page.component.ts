import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { StaffProfileResponse } from '../../services/models';
import { Api } from '../../services/api';
import { getPagesOfStaffProfile } from '../../services/functions';

@Component({
  selector: 'app-about-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './about-page.component.html',
})
export class AboutPageComponent implements OnInit {
  founders: StaffProfileResponse[] = [];
  isLoadingFounders = true;

  constructor(
    private api: Api,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadFounders();
  }

  loadFounders() {
      this.isLoadingFounders = true;
      this.api
        .invoke(getPagesOfStaffProfile, {
          page: 0,
          size: 4,
          position: 'Founder', 
          department: 'Board'
        })
        .then((res) => {
          this.founders = res?.content ?? [];
        })
        .catch(() => {
          this.founders = [];
        })
        .finally(() => {
          this.isLoadingFounders = false;
          this.cdr.markForCheck();
        });
    }
}