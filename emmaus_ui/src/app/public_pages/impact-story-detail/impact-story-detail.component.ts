import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { YoutubePlayerComponent } from '../../shared/components/youtube-player/youtube-player.component';
import { ImpactStoryResponse } from '../../services/models';
import { Api } from '../../services/api';
import { getPagesOfImpactStories } from '../../services/functions';

@Component({
  selector: 'app-impact-story-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, YoutubePlayerComponent],
  templateUrl: './impact-story-detail.component.html',
})
export class ImpactStoryDetailComponent implements OnInit {
  story: ImpactStoryResponse | undefined;
  isLoading = true;
  notFound = false;

  constructor(
    private route: ActivatedRoute,
    private api: Api,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.notFound = true;
      this.isLoading = false;
      return;
    }
    this.loadStory(id);
  }

  loadStory(publicId: string) {
    this.isLoading = true;
    // Prefer a dedicated get-by-id API if you have one.
    // Fallback: fetch a page and find by publicId (or request size large enough).
    this.api
      .invoke(getPagesOfImpactStories, {
        page: 0,
        size: 1,
        'public-id': publicId,
      })
      .then((res) => {
        const list: ImpactStoryResponse[] = res?.content ?? [];
        this.story = list.find((s) => s.publicId === publicId);
        this.notFound = !this.story;
      })
      .catch(() => {
        this.notFound = true;
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      });
  }
}