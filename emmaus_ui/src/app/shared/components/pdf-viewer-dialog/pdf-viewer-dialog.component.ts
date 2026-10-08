import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';

@Component({
  selector: 'app-pdf-viewer-dialog',
  standalone: true,
  imports: [MatDialogModule, NgxExtendedPdfViewerModule],
  template: `
    <div class="flex flex-col h-full bg-gray-900 overflow-hidden">
      <!-- Header Bar -->
      <div class="flex items-center justify-between px-4 py-3 border-b border-gray-800 text-white shrink-0">
        <h3 class="text-sm font-medium truncate">{{ data.title || 'Document Viewer' }}</h3>
        <button 
          mat-dialog-close 
          class="flex items-center justify-center w-8 h-8 rounded-lg text-gray-400 hover:text-white hover:bg-gray-800 transition">
          ✕
        </button>
      </div>

      <!-- PDF Viewer Body -->
      <div class="flex-1 w-full h-full min-h-0 overflow-hidden">
        <ngx-extended-pdf-viewer 
          [src]="data.url"
          [height]="'100%'"
          [showSidebarButton]="true"
          [showOpenFileButton]="false"
          [showPrintButton]="true"
          [showDownloadButton]="true">
        </ngx-extended-pdf-viewer>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      height: 100%;
    }
  `]
})
export class PdfViewerDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { url: string; title?: string },
    public dialogRef: MatDialogRef<PdfViewerDialogComponent>
  ) {}
}