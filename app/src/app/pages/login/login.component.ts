import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import { AuthHeaderComponent } from '../../shared/components/auth-header/auth-header.component';
import { AuthService } from 'src/app/core/services/auth.service';
import { TotpComponent } from '@pages/totp/totp.component';

@Component({
  selector: 'app-login',
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
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  isLoading = false;
  showTotpVerification = false;

  loginForm!: FormGroup;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  onLoginSubmit(): void {
    if (this.loginForm.invalid || this.isLoading) {
      this.markFormGroupTouched();
      return;
    }
    this.isLoading = true;

    const { username, password } = this.loginForm.value;

    this.authService.login(username, password).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.totpRequired) {
          this.showTotpVerification = true;
        } else {
          this.snackBar.open('Login successful!', 'Close', { duration: 2000 });
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.snackBar.open(
          err.error?.message || 'Invalid username or password.',
          'Close',
          { duration: 3000 }
        );
      },
    });
  }

  onVerifyTotpSubmit(code: string): void {
    if (this.isLoading) return;
    this.isLoading = true;

    const username = this.loginForm.value.username;

    this.authService.verifyLogin({ username, code }).subscribe({
      next: () => {
        this.isLoading = false;
        this.snackBar.open('Login successful!', 'Close', { duration: 2000 });
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.isLoading = false;
        this.snackBar.open(
          err.error?.message || 'Invalid verification code.',
          'Close',
          { duration: 3000 }
        );
      },
    });
  }

  private markFormGroupTouched(): void {
    Object.values(this.loginForm.controls).forEach((control) => {
      control.markAsTouched();
    });
  }

  getErrorMessage(controlName: string): string {
    const control = this.loginForm.get(controlName);
    if (control?.hasError('required')) {
      return 'This field is required';
    }
    if (control?.hasError('username')) {
      return 'Not a valid username';
    }
    return '';
  }
}
