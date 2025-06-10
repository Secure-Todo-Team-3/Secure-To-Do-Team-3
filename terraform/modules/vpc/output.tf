output "public_subnet_ids" {
  description = "List of public subnet IDs"
  value       = [for subnet in aws_subnet.todo_public_subnet : subnet.id]
}

output "private_subnet_ids" {
  description = "List of private subnet IDs"
  value       = [for subnet in aws_subnet.todo_private_subnet : subnet.id]
}

output "public_subnet_id" {
  description = "The first public subnet ID"
  value       = aws_subnet.todo_public_subnet[0].id
}

output "private_subnet_id" {
  description = "The first private subnet ID"
  value       = aws_subnet.todo_private_subnet[0].id
}

output "vpc_id" {
  description = "The ID of the VPC"
  value       = aws_vpc.todo_team_vpc.id
}
