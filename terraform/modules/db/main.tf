resource "aws_security_group" "todo_db_sg" {
  vpc_id = var.todo_vpc_id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["203.0.113.15/32"] # Keep your specific IP if needed for direct access
    description = "Allow direct access from specific IP"
  }


  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "todo-db-sg"
  }
}


resource "aws_db_subnet_group" "todo_subnet_group" {
  name        = "todo-db-subnet-group"
  subnet_ids = var.todo_private_subnet_ids

  tags = {
    Name = "todo-db-subnet-group"
  }
}

resource "aws_db_instance" "todo_postgres" {
  identifier              = "todo-team-db"
  engine                  = "postgres"
  engine_version          = "17.4"
  instance_class          = "db.t3.micro"
  allocated_storage       = 20
  db_name                 = var.todo_db_name
  username                = var.todo_db_username
  password                = var.todo_db_password
  db_subnet_group_name    = aws_db_subnet_group.todo_subnet_group.name
  vpc_security_group_ids  = [aws_security_group.todo_db_sg.id]
  publicly_accessible     = false
  skip_final_snapshot     = true
  deletion_protection     = false
  storage_encrypted       = true
  backup_retention_period = 7

  tags = {
    Name = "todo-team-db"
  }
}