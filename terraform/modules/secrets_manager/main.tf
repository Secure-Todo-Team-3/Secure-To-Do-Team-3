
resource "random_id" "suffix" {
  byte_length = 4
}

resource "aws_secretsmanager_secret" "rds_credentials_secret" {
  name        = "${var.name_prefix}-rds-credentials-${random_id.suffix.hex}"
  description = "RDS PostgreSQL database credentials for ${var.name_prefix} application"
  tags = {
    Name = "${var.name_prefix}-rds-credentials"
  }
}

resource "aws_secretsmanager_secret_version" "rds_credentials_secret_version" {
  secret_id = aws_secretsmanager_secret.rds_credentials_secret.id
  secret_string = jsonencode({
    username = var.db_username
    password = var.db_password
    host     = var.rds_endpoint
    dbname   = var.db_name
    port     = 5432
  })
}

resource "aws_secretsmanager_secret" "jwt_private_key_secret" {
  name        = "${var.name_prefix}-jwt-private-key-${random_id.suffix.hex}"
  description = "Base64 encoded JWT Private Key for ${var.name_prefix} application"
  recovery_window_in_days = 0
  tags = {
    Name = "${var.name_prefix}-jwt-private-key"
  }
}

resource "aws_secretsmanager_secret_version" "jwt_private_key_secret_version" {
  secret_id     = aws_secretsmanager_secret.jwt_private_key_secret.id
  secret_string = var.jwt_private_key_base64
}

resource "aws_secretsmanager_secret" "jwt_public_key_secret" {
  name        = "${var.name_prefix}-jwt-public-key-${random_id.suffix.hex}"
  description = "Base64 encoded JWT Public Key for ${var.name_prefix} application"
  recovery_window_in_days = 0
  tags = {
    Name = "${var.name_prefix}-jwt-public-key"
  }
}

resource "aws_secretsmanager_secret_version" "jwt_public_key_secret_version" {
  secret_id     = aws_secretsmanager_secret.jwt_public_key_secret.id
  secret_string = var.jwt_public_key_base64
}

resource "aws_secretsmanager_secret" "password_pepper_secret" {
  name        = "${var.name_prefix}-password-pepper-${random_id.suffix.hex}"
  description = "Password pepper for user authentication in ${var.name_prefix} application"
  recovery_window_in_days = 0
  tags = {
    Name = "${var.name_prefix}-password-pepper"
  }
}

resource "aws_secretsmanager_secret_version" "password_pepper_secret_version" {
  secret_id     = aws_secretsmanager_secret.password_pepper_secret.id
  secret_string = var.password_pepper_value
}

resource "aws_secretsmanager_secret" "field_encryption_key_secret" {
  name        = "${var.name_prefix}-field-encryption-key-${random_id.suffix.hex}"
  description = "Application field encryption key for ${var.name_prefix} application"
  recovery_window_in_days = 0 
  tags = {
    Name = "${var.name_prefix}-field-encryption-key"
  }
}

resource "aws_secretsmanager_secret_version" "field_encryption_key_secret_version" {
  secret_id     = aws_secretsmanager_secret.field_encryption_key_secret.id
  secret_string = var.field_encryption_key_value 
}