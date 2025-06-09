resource "aws_ecr_repository" "todo_repository" {
  name                 = var.todo_ecr_repository_name
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "todo-api-ecr-repo"
  }
}
