

variable "todo_vpc_id" {
  description = "The ID of the VPC."
  type        = string
}

variable "name_prefix" {
  description = "A prefix for naming EC2 resources."
  type        = string
}

variable "todo_subnet_id" {
  description = "The ID of the public subnet where the EC2 instance will be launched."
  type        = string
}

variable "todo_public_subnet_ids" {
  description = "List of public subnet IDs for EC2 related resources."
  type        = list(string)
}

variable "todo_rds_endpoint" {
  description = "The endpoint of the RDS instance."
  type        = string
}

variable "iam_instance_profile_name" {
  description = "The name of the IAM instance profile to attach to the EC2 instance. This should come from the root IAM module."
  type        = string
}

variable "todo_ecr_repository_url" {
  description = "URL of the ECR repository."
  type        = string
}

variable "todo_container_port" {
  description = "The port the Docker container will expose."
  type        = number
  default     = 80
}

variable "todo_aws_region" {
  description = "The AWS region where resources are deployed."
  type        = string
}

variable "todo_db_username" {
  description = "Database username for Secrets Manager."
  type        = string
}

variable "todo_db_password" {
  description = "Database password for Secrets Manager."
  type        = string
  sensitive   = true
}

variable "todo_totp_secret" {
  description = "TOTP secret for Secrets Manager."
  type        = string
  sensitive   = true
}

variable "todo_instance_type" {
  description = "The EC2 instance type."
  type        = string
  default     = "t3.micro"
}

variable "todo_instance_name" {
  description = "The name for the EC2 instance."
  type        = string
  default     = "todo-api"
}