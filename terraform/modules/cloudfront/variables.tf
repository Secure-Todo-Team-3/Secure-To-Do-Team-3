
variable "name_prefix" {
  description = "A unique prefix for naming resources."
  type        = string
}

/*
# Uncomment these variables if you want to use a custom domain with HTTPS.
# You will also need to provision an ACM certificate in the us-east-1 region.

variable "domain_name" {
  description = "The custom domain name for the CloudFront distribution (e.g., your-app.com)."
  type        = string
  default     = null # Set a default of null if this variable is optional
}

variable "acm_certificate_arn" {
  description = "The ARN of the ACM certificate for the custom domain. MUST be in us-east-1 region."
  type        = string
  default     = null # Set a default of null if this variable is optional
}
*/