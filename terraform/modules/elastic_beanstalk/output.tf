

output "eb_environment_url" {
  description = "The URL of the Elastic Beanstalk environment."
  value       = aws_elastic_beanstalk_environment.todo_app_env.cname
}

output "eb_instance_security_group_id" {
  description = "The ID of the security group attached to EB instances."
  value       = aws_security_group.eb_instance_sg.id
}