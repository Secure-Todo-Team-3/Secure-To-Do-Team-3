

resource "aws_security_group" "todo_ec2_sg" {
  vpc_id = var.todo_vpc_id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Consider tightening this for production
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # This should ideally be restricted to your ALB's security group
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # This should ideally be restricted to your ALB's security group
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1" # Allow all outbound traffic (adjust as needed for security)
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "todo-ec2-sg"
  }
}

resource "tls_private_key" "todo_ssh_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "random_id" "todo_suffix" {
  byte_length = 4
}

resource "aws_key_pair" "todo_key_pair" {
  key_name   = "${var.todo_instance_name}-key-${random_id.todo_suffix.hex}"
  public_key = tls_private_key.todo_ssh_key.public_key_openssh
}

resource "aws_secretsmanager_secret" "todo_secret" {
  name = "todo-app-secret"
}

resource "aws_secretsmanager_secret_version" "todo_secret_value" {
  secret_id     = aws_secretsmanager_secret.todo_secret.id
  secret_string = jsonencode({
    username    = var.todo_db_username
    password    = var.todo_db_password
    totp_secret = var.todo_totp_secret
  })
}


data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name    = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}

resource "aws_instance" "todo_app_server" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = var.todo_instance_type
  key_name                    = aws_key_pair.todo_key_pair.key_name
  subnet_id                   = var.todo_subnet_id
  vpc_security_group_ids      = [aws_security_group.todo_ec2_sg.id]
  associate_public_ip_address = true
  iam_instance_profile        = var.iam_instance_profile_name # This correctly uses the input variable from main.tf

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              amazon-linux-extras install docker -y
              service docker start
              usermod -a -G docker ec2-user

              # Fetch ECR login password and log in
              aws ecr get-login-password --region ${var.todo_aws_region} | docker login --username AWS --password-stdin ${var.todo_ecr_repository_url}

              # Fetch secrets from Secrets Manager
              SECRET_JSON=$(aws secretsmanager get-secret-value --secret-id todo-app-secret --query SecretString --output text --region ${var.todo_aws_region})
              echo "$SECRET_JSON" > /home/ec2-user/app-secrets.json
              EOF

  tags = {
    Name = "todo-team-ec2"
  }
}