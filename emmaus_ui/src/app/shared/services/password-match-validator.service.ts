import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";


export function PasswordMatchValidatorService(pass:string, confirm:string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {

    const password = control.get(pass)?.value;
    const confirmPassword = control.get(confirm)?.value;

    if (!password || !confirmPassword) {
      return null;
    }

    return password === confirmPassword
      ? null
      : { passwordMismatch: true };
  };
}