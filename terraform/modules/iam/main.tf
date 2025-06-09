# modules/iam/main.tf

resource "random_id" "suffix" {
  byte_length = 2
}

resource "aws_iam_role" "todo_app_role" {
  name = "${var.name_prefix}-todo-app-role" # Changed to use name_prefix for consistency
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Principal = {
        Service = "ec2.amazonaws.com"
      },
      Action = "sts:AssumeRole"
    }]
  })
  tags = {
    Name = "${var.name_prefix}-ec2-role"
  }
}

resource "aws_iam_policy" "todo_app_policy" {
  name   = "${var.name_prefix}-todo-app-policy" # Changed to use name_prefix for consistency
  policy = jsonencode({ # Example policy, adjust as needed
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = "s3:GetObject",
        Resource = "arn:aws:s3:::${var.name_prefix}-app-bucket/*"
      }
    ]
  })
  tags = {
    Name = "${var.name_prefix}-ec2-policy"
  }
}

resource "aws_iam_role_policy_attachment" "attach_policy" {
  role       = aws_iam_role.todo_app_role.name
  policy_arn = aws_iam_policy.todo_app_policy.arn
}

resource "aws_iam_instance_profile" "todo_instance_profile" {
  name = "${var.name_prefix}-ec2-instance-profile" # Changed to use name_prefix for consistency
  role = aws_iam_role.todo_app_role.name
  tags = {
    Name = "${var.name_prefix}-ec2-instance-profile"
  }
}


resource "aws_iam_role" "eb_service_role" {
  name = "${var.name_prefix}-eb-service-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "elasticbeanstalk.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
  tags = {
    Name = "${var.name_prefix}-eb-service-role"
  }
}

resource "aws_iam_role_policy_attachment" "eb_service_policy_attachment" {
  role       = aws_iam_role.eb_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSElasticBeanstalkService"
}

resource "aws_iam_role_policy_attachment" "eb_enhanced_health_policy_attachment" {
  role       = aws_iam_role.eb_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSElasticBeanstalkEnhancedHealth"
}


resource "aws_iam_role" "eb_instance_role" {
  name = "${var.name_prefix}-eb-instance-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
  tags = {
    Name = "${var.name_prefix}-eb-instance-role"
  }
}

resource "aws_iam_role_policy_attachment" "eb_instance_web_tier_policy_attachment" {
  role       = aws_iam_role.eb_instance_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSElasticBeanstalkWebTier"
}

resource "aws_iam_policy" "eb_rds_connect_policy" {
  name   = "${var.name_prefix}-eb-rds-connect-policy"
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "rds-db:connect",
          "rds:DescribeDBInstances"
        ],
        Resource = var.rds_instance_arn
      }
    ]
  })
  tags = {
    Name = "${var.name_prefix}-eb-rds-connect-policy"
  }
}

resource "aws_iam_role_policy_attachment" "eb_rds_connect_policy_attachment" {
  role       = aws_iam_role.eb_instance_role.name
  policy_arn = aws_iam_policy.eb_rds_connect_policy.arn
}

resource "aws_iam_policy" "eb_secrets_policy" {
  name   = "${var.name_prefix}-eb-secrets-policy"
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "secretsmanager:GetSecretValue",
          "secretsmanager:DescribeSecret"
        ],
        Resource = [
          var.rds_credentials_secret_arn,
          var.jwt_private_key_secret_arn,
          var.jwt_public_key_secret_arn,
          var.password_pepper_secret_arn
        ]
      }
    ]
  })
  tags = {
    Name = "${var.name_prefix}-eb-secrets-policy"
  }
}

resource "aws_iam_role_policy_attachment" "eb_secrets_policy_attachment" {
  role       = aws_iam_role.eb_instance_role.name
  policy_arn = aws_iam_policy.eb_secrets_policy.arn
}


resource "aws_iam_instance_profile" "eb_instance_profile" {
  name = "${var.name_prefix}-eb-profile"
  role = aws_iam_role.eb_instance_role.name
  tags = {
    Name = "${var.name_prefix}-eb-instance-profile"
  }
}