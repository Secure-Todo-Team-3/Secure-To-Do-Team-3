output "ec2_public_ip" {
  description = "The public IP address of the EC2 instance."
  value       = aws_instance.todo_app_server.public_ip
}

output "ssh_private_key" {
  description = "The private SSH key generated for EC2 access."
  value       = tls_private_key.todo_ssh_key.private_key_pem
  sensitive   = true
}

output "instance_ids" {
  description = "The IDs of the created EC2 instances."
  value       = [aws_instance.todo_app_server.id]
}

output "alb_security_group_id" {
  description = "The ID of the EC2 security group for ALB association."
  value       = aws_security_group.todo_ec2_sg.id
}