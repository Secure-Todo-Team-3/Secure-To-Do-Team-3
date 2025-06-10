variable "cidr_block" {
  description = "CIDR block for the VPC"
  type        = string
}

variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
}

variable "availability_zones" {
  type    = list(string)
  default = ["eu-west-1a", "eu-west-1b"] 
}

variable "public_subnet_cidr" {
  type = string
  default = "10.0.0.0/20" # Base for multiple /24 subnets
}

variable "private_subnet_cidr" {
  type = string
  default = "10.0.16.0/20"
}
