import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CreateProgramRequest, ProgramResponse } from '../../services/models';
import { Api } from '../../services/api';
import { createProgram, deleteProgram, getPagesOfPrograms, updateProgram } from '../../services/functions';
import { ToastrService } from 'ngx-toastr';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ButtonComponent } from '../../shared/components/ui/button/button.component';
import { ModalComponent } from '../../shared/components/ui/modal/modal.component';
import { LabelComponent } from '../../shared/components/form/label/label.component';
import { FileUploaderComponent } from '../../shared/components/file-uploader/file-uploader.component'; // adjust path if needed
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-programs',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    ButtonComponent,
    ModalComponent,
    LabelComponent,
    FileUploaderComponent
],
  templateUrl: './programs.component.html',
})
export class ProgramsComponent implements OnInit {
  programs: ProgramResponse[] = [];
  selectedProgram: ProgramResponse | undefined;

  programForm: FormGroup;

  // pagination
  currentPage = 1;
  totalPages = 1;
  pageSize = 10;
  totalElements = 0;
  isLoading = true;

  // modal
  isModalOpen = false;
  isEditMode = false;

  constructor(
    private api: Api,
    private toastr: ToastrService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private dialog:MatDialog
  ) {
    this.programForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      displayOrder: [99,Validators.required],
      logoUrl: ['', Validators.required],
      metricLabel: ['', Validators.required],
      metricNumber: [0],
      status: ['ACTIVE', Validators.required],
    });
  }

  ngOnInit(): void {
    this.loadPrograms();
  }

  // ─── List / Pagination ─────────────────────────────────────
  loadPrograms(page: number = 1) {
    this.isLoading = true;
    this.api
      .invoke(getPagesOfPrograms, {
        page: page - 1,
        size: this.pageSize,
      })
      .then((res) => {
        if (res.content) {
          this.programs = res.content;
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
    this.loadPrograms(page);
  }

  // ─── Modal helpers ─────────────────────────────────────────
  openCreateModal() {
    this.isEditMode = false;
    this.selectedProgram = undefined;
    this.programForm.reset({
      displayOrder: 0,
      metricNumber: 0,
      status: 'ACTIVE',
    });
    this.isModalOpen = true;
  }

  openEditModal(program: ProgramResponse) {
    this.isEditMode = true;
    this.selectedProgram = program;
    this.programForm.patchValue({
      name: program.name ?? '',
      description: program.description ?? '',
      displayOrder: program.displayOrder ?? 0,
      logoUrl: program.logoUrl ?? '',
      metricLabel: program.metricLabel ?? '',
      metricNumber: program.metricNumber ?? 0,
      status: program.status ?? 'ACTIVE',
    });
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.isEditMode = false;
    this.selectedProgram = undefined;
    this.programForm.reset({
      displayOrder: 99,
      metricNumber: 0,
      status: 'ACTIVE',
    });
  }

  onSubmit() {
    if (this.programForm.invalid) {
      this.programForm.markAllAsTouched();
      return;
    }
    const request: CreateProgramRequest = this.programForm.value;

    if (this.isEditMode && this.selectedProgram?.publicId) {
      this.updateProgram(this.selectedProgram.publicId, request);
    } else {
      this.createProgram(request);
    }
  }

  // ─── Create / Update ───────────────────────────────────────
  createProgram(request: CreateProgramRequest) {
    this.isLoading = true;
    this.api
      .invoke(createProgram, { body: request })
      .then(() => {
        this.toastr.success('Program Created');
        this.closeModal();
        this.loadPrograms();
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

  updateProgram(publicId: string, request: CreateProgramRequest) {
    this.isLoading = true;
    this.api
      .invoke(updateProgram, {
        'public-id': publicId,
        body: request,
      })
      .then(() => {
        this.toastr.success('Program Updated');
        this.closeModal();
        this.loadPrograms(this.currentPage);
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

  onLogoChange(url:string){
    if(url){
      this.programForm.patchValue({logoUrl: url});
    }
  }

    // ─── Delete ───────────────────────────────────────────────
    deleteProgram(publicId:string){
      this.api.invoke(deleteProgram,{
        'public-id': publicId
      }).then((res)=>{
        this.toastr.success("Program Deleted");
        this.loadPrograms(this.currentPage);
      }).catch((err) => {
        console.error(err);
        this.toastr.error(err?.error?.message || 'Request Failed');
      })
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
            this.deleteProgram(publicId)
          }
        });
      }
    }


  // ─── Helpers ───────────────────────────────────────────────
  isInvalid(controlName: string): boolean {
    const control = this.programForm.get(controlName);
    return !!control && control.invalid && control.touched;
  }

  getStatusColor(status: string | undefined): string {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':
        return 'success';
      case 'INACTIVE':
      case 'DISABLED':
        return 'error';
      case 'DRAFT':
        return 'warning';
      default:
        return 'primary';
    }
  }
}