import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { ModalComponent } from '../../../shared/components/ui/modal/modal.component';
import { ButtonComponent } from '../../../shared/components/ui/button/button.component';
import { StaffProfileResponse } from '../../../services/models';
import { Api } from '../../../services/api';
import { deleteProfile, getPagesOfStaffProfile } from '../../../services/functions';
import { Router } from '@angular/router';
import { SharedService } from '../../../shared/services/shared.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  imports: [ButtonComponent, CommonModule, ModalComponent],
  selector: 'app-staff-profile',
  templateUrl: './staff-profile.component.html',
})
export class StaffProfileComponent implements OnInit {

 staffProfiles: StaffProfileResponse[] = [];
 selectedStaff: StaffProfileResponse | undefined;

  // pagination
  currentPage = 1;
  totalPages = 1;
  pageSize = 10;
  totalElements = 0;
  isLoading = true;

  // modals
  isDetailModalOpen = false;

  constructor(
    private api: Api,
    private toastr: ToastrService,
    private router:Router,
    private cdr: ChangeDetectorRef,
    private sharedService:SharedService,
    private matDialog:MatDialog
  ) {
  }

  ngOnInit(): void {
    this.loadPagesOfStaffProfile();
  }

  // ─── List / Pagination ───────────────────────────────────────
  loadPagesOfStaffProfile(page: number = 1) {
    this.isLoading = true;
    this.api.invoke(getPagesOfStaffProfile, {
        page: page - 1,
        size: this.pageSize,
      })
      .then((res) => {
        if (res.content) {
          this.staffProfiles = res.content;
          this.currentPage = Number(res.number ?? 0) + 1;
          this.totalPages = res.totalPages ?? 1;
          this.totalElements = res.totalElements ?? 0;
        }
      })
      .catch((err) => {
        console.error(err);
        this.toastr.error(err?.error?.message || 'Request Failed');
        
      }).finally(()=>{
        this.isLoading = false;
        this.cdr.markForCheck();
      })
  }

  //ondelete
  deleteProfile(publicId:string){
    this.api.invoke(deleteProfile,{
      'public-id': publicId
    }).then(()=>{
      this.loadPagesOfStaffProfile(this.currentPage);
    }).catch((err)=>{
      console.error(err);
      this.toastr.error(err?.error?.message || 'Request Failed');
    })
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.loadPagesOfStaffProfile(page);
  }

  // ─── Create ──────────────────────────────────────────────────
  onCreateNewStaff() {
    this.router.navigate(['/admin','create-staff-profile']);
  }

  onEdit(staff:StaffProfileResponse){
    if(staff){
      this.sharedService.setStaffProfile(staff);
     this.router.navigate(['/admin','create-staff-profile'],{queryParams:{edit: true}});
    }
  }

  onDelete(publicId:any, name:any){
    if(publicId){
      const dialog = this.matDialog.open(ConfirmDialogComponent,{
          width: '400px',
          data:{
            message:`you wants to delete ${name} profile`,
            buttonName: 'Delete',
            isWarning: true
          }
      });
      dialog.afterClosed().subscribe((result) => {
        if(result){
          this.deleteProfile(publicId)
        }
      });
    }
  }


  // ─── Detail ──────────────────────────────────────────────────
  gotoDetail(staff: StaffProfileResponse) {
    this.selectedStaff = staff;
    this.isDetailModalOpen = true;
  }

  closeDetailModal() {
    this.isDetailModalOpen = false;
    this.selectedStaff = undefined;
  }

  
}
