import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { StaffProfileResponse } from '../../services/models';
import { Api } from '../../services/api';
import { createInquiry, getPagesOfStaffProfile } from '../../services/functions';

@Component({
  selector: 'app-governance-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './governance-page.component.html',
})
export class GovernancePageComponent implements OnInit {
  staff: StaffProfileResponse[] = [];
  isLoading = true;

  // Slide-over
  selected: StaffProfileResponse | null = null;
  isPanelOpen = false;

  readonly principles = [
    {
      title: 'Accountability',
      description: 'Transparent decisions and responsible stewardship of resources.',
      icon: 'scales',
    },
    {
      title: 'Integrity',
      description: 'Honesty and ethical conduct in every relationship and action.',
      icon: 'handshake',
    },
    {
      title: 'Inclusivity',
      description: 'Dignity and voice for every person we serve and work with.',
      icon: 'globe',
    },
    {
      title: 'Impact',
      description: 'Measurable change for elders, women, children, and communities.',
      icon: 'chart',
    },
  ];

  constructor(
    private api: Api,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadStaff();
  }

  loadStaff() {
    this.isLoading = true;
    this.api
      .invoke(getPagesOfStaffProfile, {
        page: 0,
        size: 50,
      })
      .then((res) => {
        const list: StaffProfileResponse[] = res?.content ?? [];
        // Prefer displayOrder if present
        this.staff = [...list].sort(
          (a, b) => (a.displayOrder ?? 999) - (b.displayOrder ?? 999)
        );
      })
      .catch(() => {
        this.staff = [];
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      });
  }

  openPanel(person: StaffProfileResponse) {
    this.selected = person;
    this.isPanelOpen = true;
    document.body.style.overflow = 'hidden';
  }

  closePanel() {
    this.isPanelOpen = false;
    this.selected = null;
    document.body.style.overflow = '';
  }

  /** Short preview for cards (description or bio) */
  previewText(person: StaffProfileResponse, max = 100): string {
    const text = person.description || person.bio || '';
    if (text.length <= max) return text;
    return text.slice(0, max).trim() + '…';
  }

  hasMore(person: StaffProfileResponse): boolean {
    const text = person.description || person.bio || '';
    return text.length > 100;
  }

  getAccentClass(index: number): string {
    return ['bg-primary', 'bg-orange', 'bg-green', 'bg-primary'][index % 4];
  }
}