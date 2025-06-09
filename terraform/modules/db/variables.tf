

variable "todo_vpc_id" {
  description = "The VPC ID for the RDS database."
  type        = string
}

variable "todo_private_subnet_ids" {
  description = "List of private subnet IDs for the RDS database."
  type        = list(string)
}

variable "todo_db_name" {
  description = "Name for the RDS database."
  type        = string
}

variable "todo_db_username" {
  description = "Username for the RDS database."
  type        = string
}

variable "todo_db_password" {
  description = "Password for the RDS database."
  type        = string
  sensitive   = true
}

