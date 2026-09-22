import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
    
    private email = new BehaviorSubject<string>('');

    /** Observable for email number */
    email$: Observable<string> = this.email.asObservable();

    
    // set email number
    setEmail(email: string): void {
      this.email.next(email);
    }

    /** Get current email number synchronously */
    get currentEmail(): string {
      return this.email.value;
    }
}