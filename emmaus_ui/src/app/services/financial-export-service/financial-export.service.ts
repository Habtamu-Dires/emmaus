import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { Api } from '../api';

export interface ExportFilters {
  
  shipmentReference?: string;
  completedDate?: string; // Format: 'YYYY-MM-DD'
  lastDays?: number;
}

@Injectable({
  providedIn: 'root'
})
export class FinancialExportService {

  private http = inject(HttpClient);

  constructor(
    private api:Api
  ) { }
  
  // Base URL matching your Spring Boot controller
  private baseUrl = environment.apiUrl + '/financial-summary';

  /**
   * Calls the backend to generate and return the export file as a Blob.
   * 
   * @param format 'excel' or 'csv'
   * @param filters The current filters applied on the UI
   */
  public downloadFinancialSummaryExport(format: 'excel' | 'csv', filters: ExportFilters): Observable<Blob> {
    let params = new HttpParams();

    // Dynamically append only the filters that have values
    if (filters.shipmentReference) {
      params = params.set('shipment-reference', filters.shipmentReference);
    }
    if (filters.completedDate) {
      params = params.set('completed-date', filters.completedDate);
    }
    if (filters.lastDays) {
      params = params.set('last-days', filters.lastDays.toString());
    }

    // Call the specific endpoint based on the requested format
    const endpoint = `${this.baseUrl}/export/${format}`;

    return this.http.get(endpoint, {
      params,
      responseType: 'blob' // <-- ABSOLUTELY REQUIRED FOR FILES
    });
  }

  public downloadDriverPaymentSummaryExport(format: 'excel' | 'csv', filters: ExportFilters): Observable<Blob> {
    let params = new HttpParams();

    // Dynamically append only the filters that have values
    if (filters.shipmentReference) {
      params = params.set('shipment-reference', filters.shipmentReference);
    }
    if (filters.completedDate) {
      params = params.set('completed-date', filters.completedDate);
    }
    if (filters.lastDays) {
      params = params.set('last-days', filters.lastDays.toString());
    }

    // Call the specific endpoint based on the requested format
    const endpoint = `${this.baseUrl}/driver-payments-summary/export/${format}`;

    return this.http.get(endpoint, {
      params,
      responseType: 'blob' // <-- ABSOLUTELY REQUIRED FOR FILES
    });

  }
}