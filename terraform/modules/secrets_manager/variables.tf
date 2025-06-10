# modules/secrets_manager/variables.tf

variable "name_prefix" {
  description = "A unique prefix for naming resources."
  type        = string
}

variable "db_username" {
  description = "Master username for the RDS database."
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Master password for the RDS database."
  type        = string
  sensitive   = true
}

variable "rds_endpoint" {
  description = "The endpoint address of the RDS database instance."
  type        = string
}

variable "db_name" {
  description = "The name of the database within the RDS instance."
  type        = string
}

variable "jwt_private_key_base64" {
  description = "Base64 encoded private key for JWT signing."
  type        = string
  sensitive   = true
}

variable "jwt_public_key_base64" {
  description = "Base64 encoded public key for JWT verification."
  type        = string
  sensitive   = true
}

variable "password_pepper_value" {
  description = "The secret string value for the password pepper."
  type        = string
  sensitive   = true
}

variable "field_encryption_key_value" {
  description = "The secret value for the application's field encryption key."
  type        = string
  sensitive   = true 
}