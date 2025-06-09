

output "rds_endpoint" {
  description = "The endpoint address of the RDS database."
  value       = aws_db_instance.todo_postgres.endpoint
}


output "db_security_group_id" {
  description = "The ID of the RDS database security group."
  value       = aws_security_group.todo_db_sg.id
}

output "db_port" {
  description = "The port of the RDS database."
  value       = aws_db_instance.todo_postgres.port
}

output "db_name" {
  description = "The name of the RDS database."
  value       = aws_db_instance.todo_postgres.db_name
}

output "db_username" {
  description = "The username for the RDS database."
  value       = aws_db_instance.todo_postgres.username
}

output "db_instance_arn" {
  description = "The ARN of the RDS database instance."
  value       = aws_db_instance.todo_postgres.arn
}