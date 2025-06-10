

variable "app_name" {
  description = "Name of the Elastic Beanstalk application."
  type        = string
}

variable "env_name" {
  description = "Name of the Elastic Beanstalk environment."
  type        = string
}

variable "name_prefix" {
  description = "Prefix for naming resources."
  type        = string
}

variable "vpc_id" {
  description = "ID of the VPC."
  type        = string
}

variable "public_subnet_ids" {
  description = "List of public subnet IDs."
  type        = list(string)
}

variable "private_subnet_ids" {
  description = "List of private subnet IDs."
  type        = list(string)
}

variable "eb_instance_profile_name" {
  description = "Name of the IAM instance profile for Elastic Beanstalk instances."
  type        = string
}

variable "container_port" {
  description = "The port the Docker container exposes (e.g., 8080 for Spring Boot)."
  type        = number
}

variable "rds_endpoint" {
  description = "The endpoint of the RDS database instance."
  type        = string
}

variable "db_name" {
  description = "The name of the database."
  type        = string
}

variable "db_username" {
  description = "The username for the database."
  type        = string
}

variable "db_password" {
  description = "The password for the database."
  type        = string
  sensitive   = true
}

variable "ecr_repository_url" {
  description = "The URL of the ECR repository containing the Docker image."
  type        = string
}

# These are the variables that were causing the "Unsupported argument" error:
variable "rds_credentials_secret_id" {
  description = "ID of the RDS credentials secret from Secrets Manager."
  type        = string
}

variable "jwt_private_key_secret_id" {
  description = "ID of the JWT private key secret from Secrets Manager."
  type        = string
}

variable "jwt_public_key_secret_id" {
  description = "ID of the JWT public key secret from Secrets Manager."
  type        = string
}

variable "password_pepper_secret_id" {
  description = "ID of the password pepper secret from Secrets Manager."
  type        = string
}