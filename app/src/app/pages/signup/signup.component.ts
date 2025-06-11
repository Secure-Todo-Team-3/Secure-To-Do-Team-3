import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthHeaderComponent } from '../../shared/components/auth-header/auth-header.component';
import { environment } from 'src/app/shared/environments/environment';
import { AuthService } from 'src/app/core/services/auth.service';
import { TotpComponent } from '@pages/totp/totp.component';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    AuthHeaderComponent,
    TotpComponent,
  ],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent {
  isLoading: boolean = false;
  totpSetupDetails: any;

  signupForm: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true;
  showTotpSetup: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar,
    private authService: AuthService
  ) {
    this.signupForm = this.fb.group(
      {
        username: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(environment.passwordPattern)]],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  private passwordMatchValidator(
    control: AbstractControl
  ): { [key: string]: boolean } | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    if (password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }

    if (confirmPassword.hasError('passwordMismatch')) {
      delete confirmPassword.errors!['passwordMismatch'];
      if (Object.keys(confirmPassword.errors!).length === 0) {
        confirmPassword.setErrors(null);
      }
    }

    return null;
  }

  onSubmit(): void {
    if (this.signupForm.invalid || this.isLoading) {
      this.markFormGroupTouched(this.signupForm);
      return;
    }
    this.isLoading = true;

    const { confirmPassword, ...registerRequest } = this.signupForm.value;

    this.authService.registerAndInitiateTotp(registerRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.totpSetupDetails = response;
        this.showTotpSetup = true;
      },
      error: (err) => {
        this.isLoading = false;
        this.snackBar.open(
          err.error?.message || 'Registration failed. Please try again.',
          'Close',
          { duration: 3000 }
        );
      },
    });
  }

  private markFormGroupTouched(signupForm: FormGroup<any>): void {
    Object.keys(this.signupForm.controls).forEach((key) => {
      const control = this.signupForm.get(key);
      control?.markAsTouched();
    });
  }
  onVerifyTotpSubmit(code: string) {
    if (this.isLoading) return;
    this.isLoading = true;

    const username = this.signupForm.value.username;
    if (!username) {
      this.snackBar.open('Username not found. Please start over.', 'Close', {
        duration: 3000,
      });
      this.isLoading = false;
      return;
    }

    this.authService.verifyRegistration({ username, code }).subscribe({
      next: () => {
        this.isLoading = false;
        this.snackBar.open('Registration successful! Welcome.', 'Close', {
          duration: 2000,
        });
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.isLoading = false;
        this.snackBar.open(
          err.error?.message || 'Verification failed. Please try again.',
          'Close',
          { duration: 3000 }
        );
      },
    });
  }

  getErrorMessage(controlName: string): string {
    const control = this.signupForm.get(controlName);

    if (control?.hasError('required')) {
      return `${this.getFieldDisplayName(controlName)} is required`;
    }

    if (control?.hasError('email')) {
      return 'Please enter a valid email address';
    }

    if (control?.hasError('minlength')) {
      const minLength = control.errors?.['minlength'].requiredLength;
      return `${this.getFieldDisplayName(
        controlName
      )} must be at least ${minLength} characters`;
    }

    if (control?.hasError('passwordMismatch')) {
      return 'Passwords do not match';
    }

    return '';
  }

  private getFieldDisplayName(controlName: string): string {
    const fieldNames: { [key: string]: string } = {
      username: 'Username',
      email: 'Email',
      password: 'Password',
      confirmPassword: 'Confirm Password',
    };
    return fieldNames[controlName] || controlName;
  }
}
