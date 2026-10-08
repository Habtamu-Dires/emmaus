import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProgramResponse } from '../../services/models';
import { Api } from '../../services/api';
import { getPagesOfPrograms } from '../../services/functions';

@Component({
  selector: 'app-programs-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './programs-page.component.html',
})
export class ProgramsPageComponent implements OnInit {
  programs: ProgramResponse[] = [];

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
    this.loadPrograms();
  }

  loadPrograms(page: number = 1) {
    this.isLoading = true;
    this.api
      .invoke(getPagesOfPrograms, {
        page: page - 1,
        size: this.pageSize,
      })
      .then((res) => {
        this.programs = res?.content ?? [];
        this.currentPage = Number(res?.number ?? 0) + 1;
        this.totalPages = res?.totalPages ?? 1;
        this.totalElements = res?.totalElements ?? 0;
      })
      .catch(() => {
        this.programs = [];
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      });
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages || page === this.currentPage) return;
    this.loadPrograms(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  getAccentClass(index: number): string {
    const accents = ['bg-primary', 'bg-orange', 'bg-green'];
    return accents[index % 3];
  }

  getAccentTextClass(index: number): string {
    const accents = ['text-primary', 'text-orange', 'text-green'];
    return accents[index % 3];
  }

  getAccentSoftClass(index: number): string {
    const accents = [
      'bg-primary/5 dark:bg-primary/10',
      'bg-orange/5 dark:bg-orange/10',
      'bg-green/5 dark:bg-green/10',
    ];
    return accents[index % 3];
  }
}
