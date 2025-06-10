

output "eb_instance_profile_name" {
  description = "The name of the Elastic Beanstalk EC2 instance profile."
  value       = aws_iam_instance_profile.eb_instance_profile.name
}

output "eb_instance_profile_arn" {
  description = "The ARN of the Elastic Beanstalk EC2 instance profile."
  value       = aws_iam_instance_profile.eb_instance_profile.arn
}

