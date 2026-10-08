import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InquiryResponse, UpdateInquiryRequest } from '../../services/models';
import { Api } from '../../services/api';
import { getPagesOfInquiries, updateInquiry } from '../../services/functions';
import { ToastrService } from 'ngx-toastr';
import { ButtonComponent } from '../../shared/components/ui/button/button.component';
import { ModalComponent } from '../../shared/components/ui/modal/modal.component';

@Component({
  selector: 'app-inquires',
  standalone: true,
  imports: [CommonModule, FormsModule, ButtonComponent, ModalComponent],
  templateUrl: './inquires.component.html',
})
export class InquiresComponent implements OnInit {
  inquires: InquiryResponse[] = [];
  selectedInquiry: InquiryResponse | undefined;

  // pagination
  currentPage = 1;
  totalPages = 1;
  pageSize = 10;
  totalElements = 0;
  isLoading = true;
  isUpdating = false;

  // modal
  isDetailModalOpen = false;

  // form fields for update
  updateStatus = '';
  updateRemark = '';

  // available statuses
  readonly statuses = ['NEW', 'SEEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];

  constructor(
    private api: Api,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadInquires();
  }

  // ─── List / Pagination ─────────────────────────────────────
  loadInquires(page: number = 1) {
    this.isLoading = true;
    this.api
      .invoke(getPagesOfInquiries, {
        page: page - 1,
        size: this.pageSize,
      })
      .then((res) => {
        if (res.content) {
          this.inquires = res.content;
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

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.loadInquires(page);
  }

  // ─── Detail modal ──────────────────────────────────────────
  openDetail(inquiry: InquiryResponse) {
    this.selectedInquiry = inquiry;
    this.updateStatus = inquiry.status || 'NEW';
    this.updateRemark = inquiry.remark || '';
    this.isDetailModalOpen = true;
  }

  closeDetailModal() {
    this.isDetailModalOpen = false;
    this.selectedInquiry = undefined;
    this.updateStatus = '';
    this.updateRemark = '';
  }

  // ─── Update status ─────────────────────────────────────────
  onUpdateStatus() {
    if (!this.selectedInquiry?.publicId) return;

    const request: UpdateInquiryRequest = {
      status: this.updateStatus,
      remark: this.updateRemark?.trim() || undefined,
    };

    this.isUpdating = true;
    this.api
      .invoke(updateInquiry, {
        'public-id': this.selectedInquiry.publicId,
        body: request,
      })
      .then(() => {
        this.toastr.success('Inquiry updated successfully');
        this.closeDetailModal();
        this.loadInquires(this.currentPage);
      })
      .catch((err) => {
        console.error(err);
        this.toastr.error(err?.error?.message || 'Failed to update inquiry');
      })
      .finally(() => {
        this.isUpdating = false;
        this.cdr.markForCheck();
      });
  }

  // ─── Helpers ───────────────────────────────────────────────
  getStatusClasses(status: string | undefined): string {
    switch (status?.toUpperCase()) {
      case 'NEW':
        return 'bg-blue-50 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400';
      case 'SEEN':
        return 'bg-gray-100 text-gray-700 dark:bg-white/10 dark:text-gray-300';
      case 'IN_PROGRESS':
        return 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-500';
      case 'RESOLVED':
        return 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-500';
      case 'CLOSED':
        return 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-500';
      default:
        return 'bg-gray-100 text-gray-700 dark:bg-white/10 dark:text-gray-300';
    }
  }
}