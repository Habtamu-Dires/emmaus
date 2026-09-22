/**
 * Reusable utility to handle Blob file downloads in the browser.
 */
export class FileDownloadUtility {
  /**
   * Triggers a browser download for a given Blob.
   * 
   * @param blob The binary data received from the backend
   * @param fileName The desired name for the downloaded file (e.g., 'report.xlsx')
   */
  public static downloadBlob(blob: Blob, fileName: string): void {
    // 1. Create a temporary URL pointing to the Blob in browser memory
    const url = window.URL.createObjectURL(blob);
    
    // 2. Create a hidden HTML anchor (<a>) element
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName;
    
    // 3. Append to DOM, click it to trigger download, and remove it
    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);
    
    // 4. Release the memory to prevent memory leaks in the browser
    setTimeout(() => {
      window.URL.revokeObjectURL(url);
    }, 100);
  }
}