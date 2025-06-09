# modules/secrets_manager/output.tf

output "rds_credentials_secret_arn" {
  description = "The ARN of the RDS database credentials secret."
  value       = aws_secretsmanager_secret.rds_credentials_secret.arn
}

output "rds_credentials_secret_id" {
  description = "The ID of the RDS database credentials secret."
  value       = aws_secretsmanager_secret.rds_credentials_secret.id
}

output "jwt_private_key_secret_arn" {
  description = "The ARN of the JWT private key secret."
  value       = aws_secretsmanager_secret.jwt_private_key_secret.arn
}

output "jwt_private_key_secret_id" {
  description = "The ID of the JWT private key secret."
  value       = aws_secretsmanager_secret.jwt_private_key_secret.id
}

output "jwt_public_key_secret_arn" {
  description = "The ARN of the JWT public key secret."
  value       = aws_secretsmanager_secret.jwt_public_key_secret.arn
}

output "jwt_public_key_secret_id" {
  description = "The ID of the JWT public key secret."
  value       = aws_secretsmanager_secret.jwt_public_key_secret.id
}

output "password_pepper_secret_arn" {
  description = "The ARN of the password pepper secret."
  value       = aws_secretsmanager_secret.password_pepper_secret.arn
}

output "password_pepper_secret_id" {
  description = "The ID of the password pepper secret."
  value       = aws_secretsmanager_secret.password_pepper_secret.id
}