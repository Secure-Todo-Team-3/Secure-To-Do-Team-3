export interface AuthenticatedResponse {
  token?: string;
  totpRequired?: boolean;
  message?: string;
}

export interface TotpSetupResponse {
  secret?: string;
  qrCodeImageUri?: string;
  message?: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface TotpVerificationRequest {
  username: string;
  code: string;
}