output "repository_url" {
  description = "The URL of the ECR repository"
  value       = aws_ecr_repository.todo_repository.repository_url
}
