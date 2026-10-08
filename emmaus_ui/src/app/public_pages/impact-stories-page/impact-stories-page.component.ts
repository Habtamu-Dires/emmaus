import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ImpactStoryResponse } from '../../services/models';
import { Api } from '../../services/api';
import { getPagesOfImpactStories } from '../../services/functions';

@Component({
  selector: 'app-impact-stories-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './impact-stories-page.component.html',
})
export class ImpactStoriesPageComponent implements OnInit {
  stories: ImpactStoryResponse[] = [];

  currentPage = 1;
  totalPages = 1;
  pageSize = 9;
  totalElements = 0;
  isLoading = true;

  constructor(
    private api: Api,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadStories();
  }

  loadStories(page: number = 1) {
    this.isLoading = true;
    this.api
      .invoke(getPagesOfImpactStories, {
        page: page - 1,
        size: this.pageSize,
      })
      .then((res) => {
        this.stories = res?.content ?? [];
        this.currentPage = Number(res?.number ?? 0) + 1;
        this.totalPages = res?.totalPages ?? 1;
        this.totalElements = res?.totalElements ?? 0;
      })
      .catch(() => {
        this.stories = [];
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      });
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages || page === this.currentPage) return;
    this.loadStories(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  getAccentClass(index: number): string {
    return ['bg-primary', 'bg-orange', 'bg-green'][index % 3];
  }

  getAccentTextClass(index: number): string {
    return ['text-primary', 'text-orange', 'text-green'][index % 3];
  }

  getAccentBtnClass(index: number): string {
    return [
      'bg-primary hover:bg-primary-light',
      'bg-orange hover:bg-orange-dark',
      'bg-green hover:bg-green-dark',
    ][index % 3];
  }
}