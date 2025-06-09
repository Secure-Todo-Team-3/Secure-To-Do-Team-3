variable "name_prefix" {
  description = "A unique prefix for naming the budget/alerting system."
  type        = string
  default     = "todoApp"
}

variable "budget_email_contacts" {
  description = "A list of email addresses to receive cost alerts."
  type        = list(string)
}

variable "cost_limit_usd" {
  description = "The total monthly cost limit in USD for which to set up alarms."
  type        = number
  default     = 50.00 
}