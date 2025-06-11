# 1. S3 Bucket for Frontend Static Assets
resource "aws_s3_bucket" "frontend_bucket" {
  bucket        = "${var.name_prefix}-frontend-assets-${random_string.suffix.result}"
  force_destroy = false 

  tags = {
    Name = "${var.name_prefix}-frontend-assets"
  }
}

# Add block public access settings
resource "aws_s3_bucket_public_access_block" "frontend_bucket_public_access_block" {
  bucket = aws_s3_bucket.frontend_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "random_string" "suffix" {
  length  = 8
  special = false
  upper   = false
  numeric = true
}

# 2. S3 Bucket Ownership Controls (Required for ACLs/Public Access)
resource "aws_s3_bucket_ownership_controls" "frontend_bucket_ownership" {
  bucket = aws_s3_bucket.frontend_bucket.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

# 3. S3 Bucket ACL (Access Control List)
# Setting this to "private" and using OAC below is the secure way.
resource "aws_s3_bucket_acl" "frontend_bucket_acl" {
  depends_on = [
    aws_s3_bucket_ownership_controls.frontend_bucket_ownership,
    aws_s3_bucket_public_access_block.frontend_bucket_public_access_block
  ]
  bucket = aws_s3_bucket.frontend_bucket.id
  acl    = "private"
}

# 4. CloudFront Origin Access Control (OAC)
# This securely allows CloudFront to access your S3 bucket without making the bucket public.
resource "aws_cloudfront_origin_access_control" "frontend_oac" {
  name                              = "${var.name_prefix}-frontend-oac"
  description                       = "OAC for secure frontend S3 bucket access"
  origin_access_control_origin_type = "s3"     # Specify S3 as the origin type
  signing_behavior                  = "always" # CloudFront will always sign requests to S3
  signing_protocol                  = "sigv4"  # Use SigV4 for signing
}

# 5. S3 Bucket Policy to Grant OAC Access to the Bucket
resource "aws_s3_bucket_policy" "frontend_bucket_policy" {
  depends_on = [
    aws_s3_bucket_acl.frontend_bucket_acl,
    aws_s3_bucket_public_access_block.frontend_bucket_public_access_block
  ]
  
  bucket = aws_s3_bucket.frontend_bucket.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "AllowCloudFrontServicePrincipalReadOnly"
        Effect    = "Allow"
        Principal = {
          Service = "cloudfront.amazonaws.com"
        }
        Action = [
          "s3:GetObject",
          "s3:ListBucket"
        ]
        Resource = [
          aws_s3_bucket.frontend_bucket.arn,
          "${aws_s3_bucket.frontend_bucket.arn}/*"
        ]
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.frontend_cdn.arn
          }
        }
      }
    ]
  })
}

# 6. CloudFront Distribution
resource "aws_cloudfront_distribution" "frontend_cdn" {
  origin {
    domain_name              = aws_s3_bucket.frontend_bucket.bucket_regional_domain_name
    origin_access_control_id = aws_cloudfront_origin_access_control.frontend_oac.id
    origin_id                = "s3-frontend-origin"

    custom_header {
      name  = "X-Requested-With"
      value = "CloudFront"
    }
  }

  enabled             = true
  is_ipv6_enabled     = true
  comment             = "CloudFront distribution for ${var.name_prefix} frontend"
  default_root_object = "browser/index.html"

  default_cache_behavior {
    target_origin_id       = "s3-frontend-origin"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD", "OPTIONS"]
    compress               = true

    forwarded_values {
      query_string = true
      headers      = ["Origin", "Authorization"]
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = 86400
    max_ttl     = 31536000
  }

  # Custom Error Responses
  custom_error_response {
    error_code            = 404
    response_code         = 200
    response_page_path    = "/browser/index.html"
    error_caching_min_ttl = 300
  }

  custom_error_response {
    error_code            = 403
    response_code         = 200
    response_page_path    = "/browser/index.html"
    error_caching_min_ttl = 300
  }

  # Viewer Certificate configuration
  viewer_certificate {
    cloudfront_default_certificate = true # Use CloudFront's default SSL certificate
  }

  
  restrictions {
    geo_restriction {
      restriction_type = "none" # No geographic restrictions by default
    }
  }

  tags = {
    Name = "${var.name_prefix}-frontend-cdn"
  }
}