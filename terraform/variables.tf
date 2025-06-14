
variable "db_name" {
  description = "The name of the PostgreSQL database."
  default     = "todo_db"
  type        = string
}

variable "db_username" {
  description = "The username for the database."
  type        = string
  sensitive   = true 
}

variable "db_password" {
  description = "The password for the database."
  type        = string
  sensitive   = true 
}


# variable "totp_secret" {
#   description = "TOTP shared secret for two-factor authentication (if used by app)."
#   type        = string
#   sensitive   = true
# }


variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  default     = "10.0.0.0/16" 
  type        = string
}

variable "public_subnet_cidr" {
  description = "Base CIDR for public subnets in the VPC module."
  type        = string
  default     = "10.0.0.0/20"
}

variable "private_subnet_cidr" { 
  description = "Base CIDR for private subnets in the VPC module."
  type        = string
  default     = "10.0.16.0/20"
}

variable "availability_zones" {
  description = "List of availability zones to use for the subnets."
  type        = list(string)
  default     = ["eu-west-1a", "eu-west-1b"] 
}


variable "instance_type" {
  description = "The type of EC2 instance to launch (to be removed after EC2 destroy)."
  default     = "t3.micro"
  type        = string 
}

variable "ami_id" {
  description = "The Amazon Machine Image (AMI) ID for the EC2 instance (to be removed after EC2 destroy)."
  default     = "ami-080b2e8ce472c5091" 
  type        = string
}

variable "instance_name" {
  description = "The name tag to assign to the EC2 instance (to be removed after EC2 destroy)."
  default     = "todo-api"
  type        = string
}



variable "acm_certificate_arn" {
  description = "ARN of the ACM certificate for ALB HTTPS (to be removed after ALB destroy)."
  type        = string
}


variable "aws_region" {
  description = "The AWS region to deploy resources in."
  type        = string
  default     = "eu-west-1"
}


variable "name_prefix" {
  description = "A unique prefix for naming all resources."
  type        = string
  default     = "todoapp" 
}

variable "db_username_value" {
  description = "The username for the RDS PostgreSQL database."
  type        = string
  sensitive   = true
}

variable "db_password_value" {
  description = "The password for the RDS PostgreSQL database."
  type        = string
  sensitive   = true
}

variable "db_name_value" {
  description = "The name of the RDS PostgreSQL database."
  type        = string
  sensitive   = true 
}

variable "jwt_private_key_base64_value" {
  description = "Base64 encoded JWT Private Key."
  type        = string
  sensitive   = true
}

variable "jwt_public_key_base64_value" {
  description = "Base64 encoded JWT Public Key."
  type        = string
  sensitive   = true
}

variable "password_pepper_string" {
  description = "A random string used as a password pepper."
  type        = string
  sensitive   = true
}

variable "field_encryption_key_value" {
  description = "The application's field encryption key for Secrets Manager."
  type        = string
  sensitive   = true
}