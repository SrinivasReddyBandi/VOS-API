# AWS SNS Configuration Guide

This document explains how to configure AWS SNS for email notifications in the VOS (Vendor Onboarding System) application.

## Prerequisites

1. AWS Account with SNS access
2. SNS Topic created in AWS Console
3. AWS credentials configured

## Step 1: Create SNS Topic

1. Log in to AWS Console
2. Navigate to **Simple Notification Service (SNS)**
3. Click **Create topic**
4. Choose **Standard** topic type
5. Enter topic name (e.g., `vos-notifications`)
6. Click **Create topic**
7. Copy the **Topic ARN** (e.g., `arn:aws:sns:us-east-1:123456789012:vos-notifications`)

## Step 2: Subscribe Email Endpoint (Optional)

If you want to receive emails directly via SNS:

1. In the SNS topic, click **Create subscription**
2. Choose **Email** as protocol
3. Enter your email address
4. Confirm subscription via email

**Note:** The application sends emails through SNS, so you may want to configure SNS to send emails via SES (Simple Email Service) for better deliverability.

## Step 3: Configure AWS Credentials

### Option A: Environment Variables (Recommended for Production)

Set these environment variables:

```bash
export AWS_REGION=us-east-1
export AWS_SNS_TOPIC_ARN=arn:aws:sns:us-east-1:123456789012:vos-notifications
export AWS_ACCESS_KEY_ID=your-access-key-id
export AWS_SECRET_ACCESS_KEY=your-secret-access-key
```

### Option B: AWS Credentials File

Create `~/.aws/credentials` (Linux/Mac) or `C:\Users\YourUsername\.aws\credentials` (Windows):

```ini
[default]
aws_access_key_id = your-access-key-id
aws_secret_access_key = your-secret-access-key
region = us-east-1
```

### Option C: IAM Role (Recommended for EC2/ECS)

If running on AWS EC2 or ECS, use an IAM role:

1. Create IAM role with `AmazonSNSFullAccess` policy
2. Attach role to EC2 instance or ECS task
3. The application will automatically use the role credentials

### Option D: Java System Properties

Set these JVM properties:

```bash
-Daws.accessKeyId=your-access-key-id
-Daws.secretKey=your-secret-access-key
-Daws.region=us-east-1
```

## Step 4: Configure Application

### For Local Development

Update `src/main/resources/application.yml`:

```yaml
aws:
  region: us-east-1
  sns:
    topic-arn: arn:aws:sns:us-east-1:123456789012:vos-notifications
```

### For Production

Set environment variables or update `src/main/resources/application-prod.yml`:

```yaml
aws:
  region: ${AWS_REGION:us-east-1}
  sns:
    topic-arn: ${AWS_SNS_TOPIC_ARN}
```

## Step 5: IAM Policy Requirements

The AWS credentials need the following permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "sns:Publish",
        "sns:GetTopicAttributes"
      ],
      "Resource": "arn:aws:sns:*:*:vos-notifications"
    }
  ]
}
```

Or use the managed policy: `AmazonSNSFullAccess` (for development) or create a custom policy with minimal permissions (for production).

## Step 6: Testing

1. Start the application
2. Create a vendor request via API
3. Check AWS SNS console for published messages
4. Verify email delivery (if configured)

## Troubleshooting

### Error: "Unable to load credentials"

- Verify AWS credentials are set correctly
- Check IAM role permissions (if using IAM role)
- Ensure credentials file exists and has correct format

### Error: "Topic does not exist"

- Verify the topic ARN is correct
- Ensure the topic exists in the specified region
- Check IAM permissions to access the topic

### Emails not being delivered

- Verify SNS topic has email subscriptions (if using direct SNS email)
- Consider configuring SNS to use SES for better deliverability
- Check SNS message delivery status in AWS Console

## Security Best Practices

1. **Never commit AWS credentials to version control**
2. Use IAM roles instead of access keys when possible
3. Rotate access keys regularly
4. Use least-privilege IAM policies
5. Enable CloudTrail for audit logging
6. Use AWS Secrets Manager or Parameter Store for credentials in production

## Additional Resources

- [AWS SNS Documentation](https://docs.aws.amazon.com/sns/)
- [AWS SDK for Java Documentation](https://docs.aws.amazon.com/sdk-for-java/)
- [AWS Credentials Configuration](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html)

