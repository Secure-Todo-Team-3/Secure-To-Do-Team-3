# modules/iam/variables.tf

variable "name_prefix" {
  description = "A unique prefix for naming resources."
  type        = string
}

variable "rds_instance_arn" {
  description = "ARN of the RDS instance for rds-db:connect permission."
  type        = string
}

variable "rds_credentials_secret_arn" {
  description = "ARN of the RDS credentials secret."
  type        = string
}

variable "jwt_private_key_secret_arn" {
  description = "ARN of the JWT private key secret."
  type        = string
}

variable "jwt_public_key_secret_arn" {
  description = "ARN of the JWT public key secret."
  type        = string
}

variable "password_pepper_secret_arn" {
  description = "ARN of the password pepper secret."
  type        = string
}

# Ensure these match if you are using the 'todo_app_role' and 'todo_instance_profile' sections
# If you don't need these, consider removing them from main.tf and variables.tf
variable "role_name" {
  description = "Name for the general EC2 role."
  type        = string
  default     = "" # Provide a default or remove if not used
}

variable "policy_name" {
  description = "Name for the general EC2 policy."
  type        = string
  default     = "" # Provide a default or remove if not used
}

variable "policy_json" {
  description = "JSON policy document for the general EC2 policy."
  type        = string
  default     = "" # Provide a default or remove if not used
}

variable "profile_name" {
  description = "Name for the general EC2 instance profile."
  type        = string
  default     = "" # Provide a default or remove if not used
}