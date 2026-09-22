import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

declare var window: any;

@Injectable({
  providedIn: 'root'
})
export class EnvironmentInitializerService {

  constructor() { }

  async initialize(){
    if(window.API_URL && window.API_URL !== '__API_URL_PLACEHOLDER__'){
      environment.apiUrl = window.API_URL || 'https:global-logistics.com/api';

      // Strip http:// or https:// from the API URL
      const isHttps = environment.apiUrl.startsWith('https');
      const cleanHostPath = environment.apiUrl.replace(/^https?:\/\//, '');
      
      // Result: wss://global-logistics.duckdns.org/api/ws/notifications
      environment.brokerUrl = `${isHttps ? 'wss' : 'ws'}://${cleanHostPath}/ws/notifications`;

    } else {
      environment.apiUrl = 'http://localhost:8088/api';
      environment.brokerUrl = 'ws://localhost:8088/api/ws/notifications';
    }

  }
}
