

output "cloudfront_domain_name" {
  description = "The domain name of the CloudFront distribution."
  value       = aws_cloudfront_distribution.frontend_cdn.domain_name
}

output "s3_frontend_bucket_id" {
  description = "The ID of the S3 bucket for frontend assets."
  value       = aws_s3_bucket.frontend_bucket.id
}