

resource "aws_elastic_beanstalk_application" "todo_app_eb" {
  name        = var.app_name
  description = "Elastic Beanstalk Application for TodoApp"
}

resource "aws_s3_bucket" "eb_app_versions_bucket" {
  bucket = "${var.name_prefix}-eb-app-versions-${random_string.suffix.result}"
}

resource "random_string" "suffix" {
  length  = 8
  special = false
  upper   = false
  numeric = true
}

data "archive_file" "app_source_zip" {
  type        = "zip"
  source_dir  = "${path.root}/app_source"
  output_path = "${path.module}/app-bundle.zip"
}

resource "aws_s3_bucket_object" "eb_app_bundle_upload" {
  bucket     = aws_s3_bucket.eb_app_versions_bucket.id
  key        = "app-bundle-${timestamp()}.zip"
  source     = data.archive_file.app_source_zip.output_path
  etag       = data.archive_file.app_source_zip.output_md5
  depends_on = [aws_s3_bucket.eb_app_versions_bucket]
}

resource "aws_elastic_beanstalk_application_version" "todo_app_version" {
  application = aws_elastic_beanstalk_application.todo_app_eb.name
  name        = "app-version-${timestamp()}"
  bucket      = aws_s3_bucket_object.eb_app_bundle_upload.bucket
  key         = aws_s3_bucket_object.eb_app_bundle_upload.key
  depends_on = [
    aws_s3_bucket_object.eb_app_bundle_upload,
    aws_elastic_beanstalk_application.todo_app_eb
  ]
}


data "aws_secretsmanager_secret_version" "rds_credentials_version" {
  secret_id = var.rds_credentials_secret_id
}

data "aws_secretsmanager_secret_version" "jwt_private_key_version" {
  secret_id = var.jwt_private_key_secret_id
}

data "aws_secretsmanager_secret_version" "jwt_public_key_version" {
  secret_id = var.jwt_public_key_secret_id
}

data "aws_secretsmanager_secret_version" "password_pepper_version" {
  secret_id = var.password_pepper_secret_id
}

resource "aws_elastic_beanstalk_environment" "todo_app_env" {
  name                = var.env_name
  application         = aws_elastic_beanstalk_application.todo_app_eb.name
  solution_stack_name = "64bit Amazon Linux 2023 v4.5.2 running Docker"

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "IamInstanceProfile"
    value     = var.eb_instance_profile_name
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = "t3.micro"
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "VPCId"
    value     = var.vpc_id
  }
  setting {
    namespace = "aws:ec2:vpc"
    name      = "Subnets"
    value     = join(",", var.private_subnet_ids)
  }
  setting {
    namespace = "aws:ec2:vpc"
    name      = "ELBSubnets"
    value     = join(",", var.public_subnet_ids)
  }
  setting {
    namespace = "aws:ec2:vpc"
    name      = "AssociatePublicIpAddress"
    value     = "false"
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "ELBScheme"
    value     = "public"
  }
  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "LoadBalancerType"
    value     = "application"
  }
  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "EnvironmentType"
    value     = "LoadBalanced"
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_DATASOURCE_URL"
    value     = "jdbc:postgresql://${jsondecode(data.aws_secretsmanager_secret_version.rds_credentials_version.secret_string).host}:5432/${jsondecode(data.aws_secretsmanager_secret_version.rds_credentials_version.secret_string).dbname}"
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_DATASOURCE_USERNAME"
    value     = jsondecode(data.aws_secretsmanager_secret_version.rds_credentials_version.secret_string).username
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_DATASOURCE_PASSWORD"
    value     = jsondecode(data.aws_secretsmanager_secret_version.rds_credentials_version.secret_string).password
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "JWT_SECRET_PRIVATE_KEY_BASE64"
    value     = data.aws_secretsmanager_secret_version.jwt_private_key_version.secret_string
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "JWT_SECRET_PUBLIC_KEY_BASE64"
    value     = data.aws_secretsmanager_secret_version.jwt_public_key_version.secret_string
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "TEAM3TODO_SECURITY_PASSWORD_PEPPER"
    value     = data.aws_secretsmanager_secret_version.password_pepper_version.secret_string
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SERVER_PORT"
    value     = var.container_port
  }

  setting {
    namespace = "aws:autoscaling:asg"
    name      = "MinSize"
    value     = "1"
  }
  setting {
    namespace = "aws:autoscaling:asg"
    name      = "MaxSize"
    value     = "2"
  }

  setting {
    namespace = "aws:elasticbeanstalk:healthreporting:system"
    name      = "SystemType"
    value     = "enhanced"
  }
  setting {
    namespace = "aws:elb:healthcheck"
    name      = "Interval"
    value     = "15"
  }
  setting {
    namespace = "aws:elb:healthcheck"
    name      = "Timeout"
    value     = "5"
  }
  setting {
    namespace = "aws:elb:healthcheck"
    name      = "UnhealthyThreshold"
    value     = "3"
  }
  setting {
    namespace = "aws:elb:healthcheck"
    name      = "HealthyThreshold"
    value     = "2"
  }
  setting {
    namespace = "aws:elb:loadbalancer"
    name      = "StickinessPolicy"
    value     = "lb"
  }
  setting {
    namespace = "aws:elasticbeanstalk:environment:process:default"
    name      = "HealthCheckPath"
    value     = "/actuator/health"
  }

  setting {
    namespace = "aws:elasticbeanstalk:cloudwatch:logs"
    name      = "StreamLogs"
    value     = "true"
  }
  setting {
    namespace = "aws:elasticbeanstalk:cloudwatch:logs"
    name      = "DeleteOnTerminate"
    value     = "true"
  }
  setting {
    namespace = "aws:elasticbeanstalk:cloudwatch:logs"
    name      = "RetentionInDays"
    value     = "7"
  }

  tags = {
    Name = "${var.name_prefix}-eb-env"
  }

  lifecycle {
    ignore_changes = [
      setting,
    ]
  }
}

resource "aws_security_group" "eb_instance_sg" {
  name        = "${var.name_prefix}-eb-instance-sg"
  description = "Security group for Elastic Beanstalk instances"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "${var.name_prefix}-eb-instance-sg"
  }
}