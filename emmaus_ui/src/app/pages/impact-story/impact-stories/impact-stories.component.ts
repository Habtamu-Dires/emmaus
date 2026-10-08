import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImpactStoryResponse } from '../../../services/models';
import { Api } from '../../../services/api';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { deleteImpactStory, getPagesOfImpactStories } from '../../../services/functions';
import { SharedService } from '../../../shared/services/shared.service';
import { ButtonComponent } from '../../../shared/components/ui/button/button.component';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { ModalComponent } from '../../../shared/components/ui/modal/modal.component';
import { YoutubePlayerComponent } from '../../../shared/components/youtube-player/youtube-player.component';

@Component({
  selector: 'app-impact-stories',
  standalone: true,
  imports: [CommonModule, ButtonComponent, ModalComponent, YoutubePlayerComponent],
  templateUrl: './impact-stories.component.html',
})
export class ImpactStoriesComponent implements OnInit {
  stories: ImpactStoryResponse[] = [];

  openVideoPlayer: boolean = false;
  videoUrl: string = '';

  // pagination
  currentPage = 1;
  totalPages = 1;
  pageSize = 10;
  totalElements = 0;
  isLoading = true;

  constructor(
    private api: Api,
    private router: Router,
    private toastr: ToastrService,
    private sharedService: SharedService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadImpactStories();
  }

  loadImpactStories(page: number = 1) {
    this.isLoading = true;
    this.api
      .invoke(getPagesOfImpactStories, {
        page: page - 1,
        size: this.pageSize,
      })
      .then((res) => {
        if (res.content) {
          this.stories = res.content;
          this.currentPage = Number(res.number ?? 0) + 1;
          this.totalPages = res.totalPages ?? 1;
          this.totalElements = res.totalElements ?? 0;
        }
      })
      .catch((err) => {
        console.error(err);
        this.toastr.error(err?.error?.message || 'Request Failed');
      })
      .finally(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      });
  }

  //delete
  deleteStory(publicId: string) {
    if (publicId) {
      this.api
        .invoke(deleteImpactStory, { 
          'public-id': publicId
         })
        .then((res) => {
          this.toastr.success('Impact Story deleted successfully');
          this.loadImpactStories(this.currentPage);
        })
        .catch((err) => {
          console.error(err);
          this.toastr.error(err?.error?.message || 'Request Failed');
        });
    }
  }

  onDelete(publicId:any, name:any){
      if(publicId){
        const dialog = this.dialog.open(ConfirmDialogComponent,{
            width: '400px',
            data:{
              message:`you wants to delete ${name} program`,
              buttonName: 'Delete',
              isWarning: true
            }
        });
        dialog.afterClosed().subscribe((result) => {
          if(result){
            this.deleteStory(publicId)
          }
        });
      }
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.loadImpactStories(page);
  }

  onCreateNewStory() {
    this.sharedService.setImpactStory(undefined as any); // clear previous
    this.router.navigate(['/admin', 'create-impact-story']);
  }

  onEdit(story: ImpactStoryResponse) {
    if (story) {
      this.sharedService.setImpactStory(story);
      this.router.navigate(['/admin', 'create-impact-story'], {
        queryParams: { edit: true },
      });
    }
  }

  onPlay(videoUrl: string) {
    this.videoUrl = videoUrl;
    this.openVideoPlayer = true;
  }

  getStatusClasses(status: string | undefined): string {
    switch (status?.toUpperCase()) {
      case 'PUBLISHED':
      case 'ACTIVE':
        return 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-500';
      case 'DRAFT':
        return 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-500';
      case 'ARCHIVED':
      case 'INACTIVE':
        return 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-500';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-white/10 dark:text-gray-300';
    }
  }
}