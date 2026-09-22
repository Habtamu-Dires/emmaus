import { ApplicationConfig, inject, Injector, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { httpInterceptor } from './services/interceptor/http.interceptor';
import { AuthService } from './services/auth-service/auth.service';
import { EnvironmentInitializerService } from './services/environment-initializer/environment-initializer.service';
import {provideToastr} from 'ngx-toastr';


export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
     provideRouter(routes),
     provideAppInitializer(() => {
      const injector = inject(Injector);
      const environmentInitializer = injector.get(EnvironmentInitializerService);
      
      return environmentInitializer.initialize();
    }),
    provideHttpClient(
      withInterceptors([httpInterceptor])
    ),
    provideAppInitializer(() => {
      const injector = inject(Injector);
      const authService = injector.get(AuthService);
      return authService.init();
    }),
    provideToastr({
      progressBar:true, 
      closeButton: true,
      newestOnTop: true,
      tapToDismiss: true,
      positionClass: 'toast-top-center',
      timeOut: 2000
    }),
    
  ]
};
