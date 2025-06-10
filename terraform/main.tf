
terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket  = "state-bucket-for-todo-app" 
    key     = "todo/terraform.tfstate"
    region  = "eu-west-1"
    encrypt = true
  }
}

provider "aws" {
  region = "eu-west-1" 
}

data "aws_availability_zones" "available" {
  state = "available"
}

module "vpc" {
  source              = "./modules/vpc"
  name_prefix         = var.name_prefix
  cidr_block          = "10.0.0.0/16"
  availability_zones  = slice(data.aws_availability_zones.available.names, 0, 2)
  public_subnet_cidr  = "10.0.0.0/20"
  private_subnet_cidr = "10.0.16.0/20"
}

module "s3_bucket" {
  source      = "./modules/s3"
  bucket_name = "${var.name_prefix}-app-bucket" 
  name_prefix = var.name_prefix
}

module "rds_postgres" {
  source              = "./modules/db"
  todo_vpc_id         = module.vpc.vpc_id
  todo_private_subnet_ids = module.vpc.private_subnet_ids
  todo_db_name        = var.db_name_value
  todo_db_username    = var.db_username_value
  todo_db_password    = var.db_password_value
  depends_on          = [module.vpc]
}

module "secrets_manager" {
  source      = "./modules/secrets_manager"
  name_prefix = var.name_prefix

  db_username            = var.db_username_value
  db_password            = var.db_password_value
  rds_endpoint           = module.rds_postgres.rds_endpoint
  db_name                = var.db_name_value

  jwt_private_key_base64 = var.jwt_private_key_base64_value
  jwt_public_key_base64  = var.jwt_public_key_base64_value
  password_pepper_value  = var.password_pepper_string
}

module "ecr" {
  source             = "./modules/ecr"
  todo_ecr_repository_name = "${var.name_prefix}-api-repository"
}

module "iam" {
  source             = "./modules/iam"
  name_prefix        = var.name_prefix

  rds_credentials_secret_arn = module.secrets_manager.rds_credentials_secret_arn
  jwt_private_key_secret_arn = module.secrets_manager.jwt_private_key_secret_arn
  jwt_public_key_secret_arn  = module.secrets_manager.jwt_public_key_secret_arn
  password_pepper_secret_arn = module.secrets_manager.password_pepper_secret_arn

  rds_instance_arn = module.rds_postgres.db_instance_arn 
}

module "elastic_beanstalk" {
  source               = "./modules/elastic_beanstalk"
  app_name             = "${var.name_prefix}-TodoApp"
  env_name             = "${var.name_prefix}-TodoApp-Env"
  name_prefix          = var.name_prefix
  vpc_id               = module.vpc.vpc_id
  public_subnet_ids    = module.vpc.public_subnet_ids
  private_subnet_ids   = module.vpc.private_subnet_ids
  ecr_repository_url   = module.ecr.repository_url
  eb_instance_profile_name = module.iam.eb_instance_profile_name 
  container_port       = 8080

  rds_endpoint               = module.rds_postgres.rds_endpoint
  db_name                    = var.db_name_value
  db_username                = var.db_username_value
  db_password                = var.db_password_value

 
  rds_credentials_secret_id = module.secrets_manager.rds_credentials_secret_id
  jwt_private_key_secret_id = module.secrets_manager.jwt_private_key_secret_id
  jwt_public_key_secret_id  = module.secrets_manager.jwt_public_key_secret_id
  password_pepper_secret_id = module.secrets_manager.password_pepper_secret_id

  depends_on = [
    module.vpc,
    module.iam,
    module.ecr,
    module.rds_postgres,
    module.s3_bucket,
    module.secrets_manager,
  ]
}

resource "aws_security_group_rule" "allow_eb_to_db_sg_root" {
  type                     = "ingress"
  from_port                = 5432
  to_port                  = 5432
  protocol                 = "tcp"
  source_security_group_id = module.elastic_beanstalk.eb_instance_security_group_id 
  security_group_id        = module.rds_postgres.db_security_group_id 
  description              = "Allow connections from Elastic Beanstalk instances to RDS DB"
}

module "cloudfront" {
  source      = "./modules/cloudfront"
  name_prefix = var.name_prefix 
}


module "aws_budget" {
  source = "./modules/budget"
  name_prefix = var.name_prefix
  budget_email_contacts = [
    "Gregory.Maselle@bbd.co.za", 
    "Manqoba.Makhoba@bbd.co.za",    
    "Mamatankane.Phahlamohlaka@bbd.co.za",    
    "Noluthando.Gumede@bbd.co.za"            
  ]
  cost_limit_usd = 50.00
}
