import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { ImageViewerComponent } from '../image-viewer/image-viewer.component';
import { PdfViewerDialogComponent } from '../pdf-viewer-dialog/pdf-viewer-dialog.component';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-file-reader',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-reader.component.html'
})
export class FileReaderComponent {

  @Input() fileUrl: string | null = null;
  @Input() title?: string; // Optional custom display title/filename
  @Input() accentColor: string = 'primary'; 

  constructor(private dialog: MatDialog) {}

  get isPdf(): boolean {
    return !!this.fileUrl && /\.pdf(\?.*)?$/i.test(this.fileUrl);
  }

  get isImage(): boolean {
    return !!this.fileUrl && /\.(jpg|jpeg|png|webp|gif|svg)(\?.*)?$/i.test(this.fileUrl);
  }

  get fileName(): string {
    if (this.title) return this.title;
    if (this.fileUrl) {
      return this.extractFileName(this.fileUrl);
    }
    return 'Document';
  }

  get fileExtension(): string {
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

  /**
   * View Button Handler:
   * - If PDF: opens PdfViewerDialogComponent
   * - If Image: opens ImageViewerComponent
   * - For other types (Word/Excel/etc.): downloads the file
   */
  viewFile(): void {
    if (!this.fileUrl) return;

    if (this.isPdf) {
      this.dialog.open(PdfViewerDialogComponent, {
        data: { url: this.fileUrl, title: this.fileName },
        width: '90vw',
        maxWidth: '1200px',
        height: '100vh'
      });
    } else if (this.isImage) {
      this.dialog.open(ImageViewerComponent, {
        data: { url: this.fileUrl }
      });
    } else {
      // For Word, Excel, etc., viewing directly in browser is not supported, so download it
      this.downloadFile();
    }
  }

  /**
   * Download Button Handler:
   * Triggers a browser download for any file type.
   */
  downloadFile(): void {
    if (!this.fileUrl) return;

    const link = document.createElement('a');
    link.href = this.fileUrl;
    link.target = '_blank';
    link.download = this.fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  private extractFileName(urlStr: string): string {
    try {
      const url = new URL(urlStr);
      return decodeURIComponent(url.pathname.split('/').pop() || '');
    } catch {
      return urlStr.split('/').pop() || 'File';
    }
  }
}