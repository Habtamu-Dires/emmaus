import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DocumentResponse } from '../../services/models';
import { Api } from '../../services/api';
import { getPagesOfDocuments } from '../../services/functions';


@Component({
  selector: 'app-resources-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './resources-page.component.html',
})
export class ResourcesPageComponent implements OnInit {
  documents: DocumentResponse[] = [];

  currentPage = 1;
  totalPages = 1;
  pageSize = 12;
  totalElements = 0;
  isLoading = true;

  /** Category-style cards (static intro, like the mockup top row) */
  readonly categories = [
    {
      title: 'Annual Reports',
      description: 'Yearly achievements, financials, and impact metrics.',
      color: 'primary',
      icon: 'chart',
    },
    {
      title: 'Bylaws & Governance',
      description: 'Our structure, accountability, and leadership policies.',
      color: 'orange',
      icon: 'scales',
    },
    {
      title: 'Training Materials',
      description: 'Guides for protection and GBV prevention.',
      color: 'green',
      icon: 'book',
    },
  ];

  constructor(
    private api: Api,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDocuments();
  }

  loadDocuments(page: number = 1) {
    this.isLoading = true;
    this.api
      .invoke(getPagesOfDocuments, {
        page: page - 1,
        size: this.pageSize,
      })
      .then((res) => {
        this.documents = res?.content ?? [];
        this.currentPage = Number(res?.number ?? 0) + 1;
        this.totalPages = res?.totalPages ?? 1;
        this.totalElements = res?.totalElements ?? 0;
      })
      .catch(() => {
        this.documents = [];
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      });
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages || page === this.currentPage) return;
    this.loadDocuments(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  getAccentClass(index: number): string {
    return ['bg-primary', 'bg-orange', 'bg-green'][index % 3];
  }

  getAccentTextClass(index: number): string {
    return ['text-primary', 'text-orange', 'text-green'][index % 3];
  }

  getAccentSoftClass(index: number): string {
    return [
      'bg-primary/5 dark:bg-primary/10',
      'bg-orange/5 dark:bg-orange/10',
      'bg-green/5 dark:bg-green/10',
    ][index % 3];
  }

  categoryHeaderClass(color: string): string {
    switch (color) {
      case 'orange':
        return 'bg-orange';
      case 'green':
        return 'bg-green';
      default:
        return 'bg-primary';
    }
  }

  categoryBtnClass(color: string): string {
    switch (color) {
      case 'orange':
        return 'bg-orange hover:bg-orange-dark';
      case 'green':
        return 'bg-green hover:bg-green-dark';
      default:
        return 'bg-primary hover:bg-primary-light';
    }
  }
}