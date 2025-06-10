variable "bucket_name" {
  description = "S3 bucket name"
  type        = string
}

variable "todo_versioning_enabled" {
  description = "Enable or disable versioning on the S3 bucket"
  type        = bool
  default     = true
}

variable "todo_encryption_algorithm" {
  description = "The encryption algorithm for server-side encryption"
  type        = string
  default     = "AES256"
}

variable "name_prefix" {
  description = "Prefix for resource names to ensure uniqueness"
  type        = string
}
