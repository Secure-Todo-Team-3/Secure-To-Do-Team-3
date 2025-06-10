output "todo_bucket_name" {
  description = "The name of the S3 bucket"
  value       = aws_s3_bucket.todo_storage_bucket.bucket
}

output "todo_bucket_arn" {
  description = "The ARN of the S3 bucket"
  value       = aws_s3_bucket.todo_storage_bucket.arn
}
