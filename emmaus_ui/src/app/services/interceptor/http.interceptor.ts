import { HttpErrorResponse, HttpHeaders, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, from, switchMap, throwError } from 'rxjs';
import { AuthService } from '../auth-service/auth.service'; 

export const httpInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const addToken = (request: HttpRequest<any>, token: string) =>
    request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

  const token = authService.getToken();
  const authReq = token ? addToken(req, token) : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {

      // Never try to refresh when the refresh request itself fails
      if (req.url.includes('/auth/refresh')) {
        return throwError(() => error);
      }

      if (error.status !== 401 || !authService.hasRefreshToken) {
        return throwError(() => error);
      }

      return from(authService.refreshToken()).pipe(
        switchMap((refreshed) => {

          if (!refreshed) {
            authService.logout();
            return throwError(() => error);
          }

          const newToken = authService.getToken();

          if (!newToken) {
            authService.logout();
            return throwError(() => error);
          }

          return next(addToken(req, newToken));
        }),

        catchError((refreshError) => {
          authService.logout();
          return throwError(() => refreshError);
        })
      );
    })
  );
};

// export const httpInterceptor: HttpInterceptorFn = (req, next) => {
//   const authService = inject(AuthService);

//   const addToken = (request: HttpRequest<any>, token: string) =>
//     request.clone({
//       setHeaders: {
//         Authorization: `Bearer ${token}`
//       }
//     });

//   const token = authService.getToken();

//   const authReq = token ? addToken(req, token) : req;

//   return next(authReq).pipe(
//     catchError((error: HttpErrorResponse) => {

//        if (error.status !== 401 || !authService.hasRefreshToken) {
//           return throwError(() => error);
//        } 
         
//       return from(authService.refreshToken()).pipe(
//         switchMap((refreshed) => {

//           if (refreshed) {
//             const newToken = authService.getToken();
//             return next(addToken(req, newToken as string)); // retry original request
//           }

//           authService.logout();
//           return throwError(() => error);
//         }),
//         catchError((refreshError) => {
//           authService.logout();
//           return throwError(() => refreshError);
//         })
//       );
      

//       return throwError(() => error);
//     })
//   );
// };
