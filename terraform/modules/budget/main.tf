# Define local variables for thresholds and other configurations
locals {
  # First, calculate the numeric thresholds as before
  alarm_thresholds_numeric = [for p in range(10, 101, 10) : ceil(var.cost_limit_usd * p / 100)]

  # Now, convert these numeric thresholds into a set of strings for for_each
  # Each element in the set will be a string like "5.0", "10.0", etc.
  alarm_thresholds_for_each = toset([for val in local.alarm_thresholds_numeric : tostring(val)])
}

# 1. Create an SNS Topic for notifications
resource "aws_sns_topic" "cost_alerts_topic" {
  name = "${var.name_prefix}-cost-alerts"
  tags = {
    Name        = "${var.name_prefix}-cost-alerts"
    Environment = "production" # Or your specific environment tag
  }
}

# 2. Subscribe email contacts to the SNS Topic
resource "aws_sns_topic_subscription" "email_subscriptions" {
  for_each  = toset(var.budget_email_contacts)
  topic_arn = aws_sns_topic.cost_alerts_topic.arn
  protocol  = "email"
  endpoint  = each.key # The email address from the list
  # IMPORTANT: Subscribers will receive a confirmation email. They MUST click the link to confirm the subscription.
}

# 3. Create CloudWatch Metric Alarms for cost thresholds
resource "aws_cloudwatch_metric_alarm" "monthly_cost_alarm" {
  # Use the set of string thresholds for for_each
  for_each = local.alarm_thresholds_for_each

  # Use each.key (which is a string) and convert it back to a number for the threshold attribute
  alarm_name          = "${var.name_prefix}-monthly-cost-alarm-${each.key}USD"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = 1
  metric_name         = "EstimatedCharges"
  namespace           = "AWS/Billing"
  period              = 21600 # 6 hours (in seconds), for cost monitoring accuracy
  statistic           = "Maximum"
  threshold           = tonumber(each.key) # Convert back to a number here!

  # Dimensions for filtering costs (optional, but good practice for specific services/regions)
  # Remove if you want total account cost without filtering.
  dimensions = {
    Currency = "USD"
  }

  alarm_description = "Alarm when estimated monthly charges exceed ${each.key} USD."
  alarm_actions     = [aws_sns_topic.cost_alerts_topic.arn]
  ok_actions        = [aws_sns_topic.cost_alerts_topic.arn] # Optionally send OK notifications
  insufficient_data_actions = [] # No actions for insufficient data
}