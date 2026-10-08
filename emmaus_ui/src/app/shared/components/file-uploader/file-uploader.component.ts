import { ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { HttpEventType } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { ImageViewerComponent } from '../image-viewer/image-viewer.component';
import { FileService } from '../../../services/file-service/file.service';
import { PdfViewerDialogComponent } from '../pdf-viewer-dialog/pdf-viewer-dialog.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-file-uploader',
  imports:[CommonModule],
  standalone: true,
  templateUrl: './file-uploader.component.html'
})
export class FileUploaderComponent {

  @Input() fileUrl: string | null = null;
  @Input() folder = 'others';
  @Input() acceptedTypes = 'image/*,.pdf,.doc,.docx,.xls,.xlsx';
  @Input() maxSizeBytes = 15 * 1024 * 1024; // 15 MB default
  @Input() isReadMode : boolean = false;

  @Output() fileChanged = new EventEmitter<string>();

  // Use ViewChild instead of global document.getElementById to keep multiple instances isolated
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  selectedFile: File | null = null;
  previewUrl: string | null = null;
  uploading = false;
  uploadProgress = 0;
  errorMessage = '';

  private oldFileUrl: string | null = null;

  constructor(
    private fileService: FileService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  get currentUrl(): string | null {
    return this.previewUrl || this.fileUrl;
  }

  get isImage(): boolean {
    if (this.selectedFile) {
      return this.selectedFile.type.startsWith('image/');
    }
    const url = this.currentUrl;
    return !!url && /\.(jpg|jpeg|png|webp|gif|svg)(\?.*)?$/i.test(url);
  }

  get isPdf(): boolean {
    if (this.selectedFile) {
      return this.selectedFile.type === 'application/pdf';
    }
    const url = this.currentUrl;
    return !!url && /\.pdf(\?.*)?$/i.test(url);
  }

  get fileExtension(): string {
    if (this.selectedFile) {
      return this.selectedFile.name.split('.').pop()?.toUpperCase() || 'FILE';
    }
    if (this.fileUrl) {
      try {
        const pathname = new URL(this.fileUrl).pathname;
        return pathname.split('.').pop()?.toUpperCase() || 'FILE';
      } catch {
        return 'FILE';
      }
    }
    return 'FILE';
  }

  get fileName(): string {
    if (this.selectedFile) {
      return this.selectedFile.name;
    }
    if (this.fileUrl) {
      return this.extractFileName(this.fileUrl);
    }
    return '';
  }

  triggerFileInput(): void {
    if (this.fileInput) {
      this.fileInput.nativeElement.value = ''; // Reset to allow re-selecting same file if canceled
      this.fileInput.nativeElement.click();
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.errorMessage = '';

    if (file.size > this.maxSizeBytes) {
      const maxMb = (this.maxSizeBytes / (1024 * 1024)).toFixed(0);
      this.handleUploadError(`File must be smaller than ${maxMb} MB.`);
      return;
    }

    this.selectedFile = file;

    if (this.fileUrl && !this.oldFileUrl) {
      this.oldFileUrl = this.fileUrl;
    }

    if (this.previewUrl) {
      URL.revokeObjectURL(this.previewUrl);
    }

    this.previewUrl = URL.createObjectURL(file);
    this.uploadProgress = 0;
    this.cdr.detectChanges();
  }

  async upload(): Promise<void> {
    if (!this.selectedFile) return;

    this.uploading = true;
    this.errorMessage = '';
    this.uploadProgress = 0;
    this.cdr.detectChanges();

    try {
      const file = this.selectedFile;
      const presigned = await this.fileService.getPreSignedUrl(this.folder, file.name, file.type,);
      
      const upload$ = this.fileService.uploadFileToCloud(
        presigned.preSignedUrl as string, 
        file
      );

      upload$.subscribe({
        next: (event: any) => {
          try {
            if (event.type === HttpEventType.UploadProgress && event.total) {
              this.uploadProgress = Math.round((100 * event.loaded) / event.total);
              this.cdr.detectChanges();
            }
            if (event.type === HttpEventType.Response) {
              // Accept 2xx status codes (200 OK, 201 Created, 204 No Content for S3)
              if (event.status >= 200 && event.status < 300) {
                this.uploadProgress = 100;
                const newUrl = presigned.publicUrl;

                if (this.oldFileUrl && this.oldFileUrl !== newUrl) {
                  this.deleteOldFile(this.oldFileUrl).catch(err => 
                    console.error('Failed to delete old file:', err)
                  );
                }

                this.fileUrl = newUrl as string;
                this.selectedFile = null;
                this.oldFileUrl = null;
                if (this.previewUrl) {
                  URL.revokeObjectURL(this.previewUrl);
                  this.previewUrl = null;
                }
                this.uploading = false;
                this.fileChanged.emit(newUrl);
                this.cdr.detectChanges();
              } else {
                this.handleUploadError(`Upload failed with server status ${event.status}.`);
              }
            }
          } catch (err) {
            console.error('Error during upload progress handling:', err);
            this.handleUploadError('An error occurred while processing the upload.');
          }
        },
        error: (error) => {
          console.error('Upload stream error:', error);
          this.handleUploadError('Failed to upload file. Please check your network connection or cloud bucket CORS configuration.');
        }
      });
    } catch (error) {
      console.error('Failed to prepare upload:', error);
      this.handleUploadError('Could not prepare file upload. Please try again.');
    }
  }

  private handleUploadError(message: string): void {
    this.errorMessage = message;
    this.uploading = false;
    this.uploadProgress = 0;
    this.cdr.detectChanges(); // Force UI update immediately so progress bar removes
  }

  viewFile(): void {
    const url = this.currentUrl;
    if (!url) return;

    if (this.isImage) {
      this.dialog.open(ImageViewerComponent, { data: { url } });
    } else if (this.isPdf) {
      this.dialog.open(PdfViewerDialogComponent, {
        data: { url, title: this.fileName },
        width: '90vw',
        maxWidth: '1200px',
        height: '100vh'
      });
    } else {
      window.open(url, '_blank');
    }
  }

  changeFile(): void {
    this.triggerFileInput();
  }

  removeSelection(): void {
    this.selectedFile = null;
    if (this.previewUrl) {
      URL.revokeObjectURL(this.previewUrl);
      this.previewUrl = null;
    }
    this.uploadProgress = 0;
    this.errorMessage = '';
    this.cdr.detectChanges();
  }

  private async deleteOldFile(fileUrl: string): Promise<void> {
    try {
      const fileName = this.extractFileName(fileUrl);
      const deleteUrl = await this.fileService.getPresignedUrlForDelete(fileName, this.folder);
      await this.fileService.deleteFile(deleteUrl);
    } catch (e) {
      console.error('Failed to delete old file:', e);
    }
  }

  private extractFileName(fileUrl: string): string {
    try {
      const url = new URL(fileUrl);
      return decodeURIComponent(url.pathname.split('/').pop() || '');
    } catch {
      return fileUrl.split('/').pop() || '';
    }
  }
}