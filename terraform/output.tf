

output "vpc_id" {
  description = "The ID of the VPC."
  value       = module.vpc.vpc_id
}

output "rds_endpoint" {
  description = "The endpoint of the RDS instance."
  value       = module.rds_postgres.rds_endpoint
}

output "ecr_repository_url" {
  description = "URL of the ECR repository."
  value       = module.ecr.repository_url
}


output "elastic_beanstalk_url" {
  description = "The URL of the Elastic Beanstalk environment."
  value       = module.elastic_beanstalk.eb_environment_url
}


output "frontend_cdn_url" {
  description = "The URL of the CloudFront distribution for the frontend."
  value       = "https://${module.cloudfront.cloudfront_domain_name}"
}


output "frontend_s3_bucket_id" {
  description = "The S3 bucket ID for uploading frontend assets."
  value       = module.cloudfront.s3_frontend_bucket_id
}

output "rds_credentials_secret_arn" {
  value = module.secrets_manager.rds_credentials_secret_arn
}