import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Api } from '../../services/api';
import { CreateDocumentRequest, DocumentResponse } from '../../services/models';
import { createDocument, deleteDocument, getPagesOfDocuments, updateDocument } from '../../services/functions';
import { ToastrService } from 'ngx-toastr';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ButtonComponent } from '../../shared/components/ui/button/button.component';
import { ModalComponent } from '../../shared/components/ui/modal/modal.component';
import { LabelComponent } from '../../shared/components/form/label/label.component';
import { FileUploaderComponent } from '../../shared/components/file-uploader/file-uploader.component';
import { PdfViewerDialogComponent } from '../../shared/components/pdf-viewer-dialog/pdf-viewer-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  imports: [ReactiveFormsModule, CommonModule, ButtonComponent, ModalComponent, LabelComponent, FileUploaderComponent],
  selector: 'app-document',
  templateUrl: './document.component.html',
})
export class DocumentComponent implements OnInit{

  documents:DocumentResponse[] = [];
  selectedDocument:DocumentResponse | undefined;

  createDocumentForm:FormGroup;

  //pagination
  currentPage = 1;
  totalPages = 1;
  pageSize = 10;
  totalElements = 0;
  isLoading = true;
  
  // modal 
  isModalOpen = false;
  isEditMode = false;

  constructor(
    private api:Api,
    private toastr:ToastrService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog,
    private fb:FormBuilder
  ){
    this.createDocumentForm = this.fb.group({
      description: ['',Validators.required],
      displayOrder: [99],
      documentType: ['',Validators.required],
      publishedDate: ['',Validators.required],
      thumbnailUrl: ['',Validators.required],
      title: ['',Validators.required],
      url: ['',Validators.required],
    });
  }

  ngOnInit(): void {
    this.loadDocuments();
  }

  // ─── Modal helpers ─────────────────────────────────────────
  openCreateModal() {
    this.isEditMode = false;
    this.selectedDocument = undefined;
    this.createDocumentForm.reset({ displayOrder: 0 });
    this.isModalOpen = true;
  }

  openEditModal(doc: DocumentResponse) {
    this.isEditMode = true;
    this.selectedDocument = doc;
    this.createDocumentForm.patchValue({
      title: doc.title ?? '',
      description: doc.description ?? '',
      documentType: doc.documentType ?? '',
      publishedDate: doc.publishedDate ? doc.publishedDate.substring(0, 10) : '', // yyyy-MM-dd for input[type=date]
      url: doc.url ?? '',
      thumbnailUrl: doc.thumbnailUrl ?? '',
      displayOrder: doc.displayOrder ?? 0,
    });
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.isEditMode = false;
    this.selectedDocument = undefined;
    this.createDocumentForm.reset({ displayOrder: 0 });
  }

  onSubmit() {
    if (this.createDocumentForm.invalid) {
      this.createDocumentForm.markAllAsTouched();
      return;
    }
    const request: CreateDocumentRequest = this.createDocumentForm.value;

    if (this.isEditMode) {
      this.updateDocument(request);
    } else {
      this.createDocument(request);
    }
  }

  // update your existing methods slightly:
  loadDocuments(page: number = 1) {
    this.isLoading = true;
    this.api
      .invoke(getPagesOfDocuments, { page: page - 1, size: this.pageSize })
      .then((res) => {
        if (res.content) {
          this.documents = res.content;
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

  createDocument(request: CreateDocumentRequest) {
    this.isLoading = true;
    this.api
      .invoke(createDocument, { body: request })
      .then(() => {
        this.toastr.success('Document Created');
        this.closeModal();
        this.loadDocuments(1);
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

  updateDocument(request: CreateDocumentRequest) {
    if (!this.selectedDocument?.publicId) return;
    this.isLoading = true;
    this.api
      .invoke(updateDocument, {
        'public-id': this.selectedDocument.publicId,
        body: request,
      })
      .then(() => {
        this.toastr.success('Document Updated');
        this.closeModal();
        this.loadDocuments(this.currentPage);
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

  deleteDocument(publicId:string){
    this.api.invoke(deleteDocument,{
      'public-id': publicId
    }).then((res)=>{
      this.toastr.success("Document deleted");
      this.loadDocuments(this.currentPage);
    }).catch((err)=>{
      console.error(err);
      this.toastr.error(err?.error?.message || 'Request Failed');
    })
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.loadDocuments(page);
  }

  isInvalid(controlName: string): boolean {
    const control = this.createDocumentForm.get(controlName);
    return !!control && control.invalid && control.touched;
  }

  onDocUploaded(url:string){
    if(url){
      this.createDocumentForm.patchValue({url:url});
    }
  }

  onThumbnailUploaded(thumbnailUrl:string){
    if(thumbnailUrl){
      this.createDocumentForm.patchValue({thumbnailUrl:thumbnailUrl});
    }
  }

  viewFile(url:any,title:any): void {
  
      if (!url) return;
      
      this.dialog.open(PdfViewerDialogComponent, {
        data: { url, title: title },
        width: '90vw',
        maxWidth: '1200px',
        height: '100vh'
      });
     
    }

  onDelete(publicId:any, name:any){
      if(publicId){
        const dialog = this.dialog.open(ConfirmDialogComponent,{
            width: '400px',
            data:{
              message:`you wants to delete ${name} document`,
              buttonName: 'Delete',
              isWarning: true
            }
        });
        dialog.afterClosed().subscribe((result) => {
          if(result){
            this.deleteDocument(publicId)
          }
        });
      }
    }
}
