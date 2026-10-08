import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface ImageUrl {
  url?: string;
}

@Component({
  selector: 'app-image-viewer',
  imports: [],
  templateUrl: './image-viewer.component.html',
  styles:``
})
export class ImageViewerComponent {
  
  zoomLevel = 1.0;

  constructor(
    public dialogRef: MatDialogRef<ImageViewerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ImageUrl
  ) {}

  onConfirm() {
    this.dialogRef.close({ 
      close: true
    });
  }

  onCancel() {
    this.dialogRef.close({ close: false });
  }

  zoomIn() {
    if (this.zoomLevel < 3) this.zoomLevel += 0.2;
  }

  zoomOut() {
    if (this.zoomLevel > 0.5) this.zoomLevel -= 0.2;
  }

  resetZoom() {
    this.zoomLevel = 1.0;
  }

  // Optional: Zoom with mouse wheel
  onMouseWheel(event: WheelEvent) {
    event.preventDefault();
    event.deltaY < 0 ? this.zoomIn() : this.zoomOut();
  }
}
