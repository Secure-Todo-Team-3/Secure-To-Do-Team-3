import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-totp',
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
  ],
  templateUrl: './totp.component.html',
  styleUrl: './totp.component.scss',
})
export class TotpComponent {
  @Input() username: string = '';

  @Input() qrCodeImageUri: string | null = null;

  @Input() cardTitle: string = 'Two-Factor Verification';

  @Output() verify = new EventEmitter<string>();

  totpForm!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.totpForm = this.fb.group({
      code: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(6),
          Validators.pattern('^[0-9]*$'),
        ],
      ],
    });
  }

  onSubmit(): void {
    if (this.totpForm.invalid) {
      return;
    }
    this.verify.emit(this.totpForm.value.code);
  }
}
