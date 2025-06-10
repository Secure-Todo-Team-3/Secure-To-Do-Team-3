resource "aws_s3_bucket" "todo_storage_bucket" {
  bucket = "${var.name_prefix}-app-bucket-${random_id.bucket_suffix.hex}"

  tags = {
    Name = var.bucket_name
  }
}

resource "random_id" "bucket_suffix" {
  byte_length = 4
}

resource "aws_s3_bucket_versioning" "todo_versioning" {
  bucket = aws_s3_bucket.todo_storage_bucket.id

  versioning_configuration {
    status = var.todo_versioning_enabled ? "Enabled" : "Suspended"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "todo_encryption" {
  bucket = aws_s3_bucket.todo_storage_bucket.bucket

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = var.todo_encryption_algorithm
    }
  }
}
