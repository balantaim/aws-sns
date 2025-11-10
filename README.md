# Amazon Simple Notification Service (SNS) demo

## Description

This Spring Boot application demonstrates how to integrate **Amazon Simple Notification Service (SNS)** with a **Spring Cloud AWS** application. It allows sending and subscribing to notifications via **Email** and **SMS** protocols using configurable AWS credentials and region properties.

### Software and Requirements

**Tools:** Java, Spring, Lombok, Maven, AWS cloud and SNS

**Requirements:**

* AWS account
* IAM User with permissions to publish and subscribe to topics
* SNS topic (`Standard` type instead of `FIFO`)

### Official Documentation

* [Docs for Spring Boot 3.4.0](https://docs.awspring.io/spring-cloud-aws/docs/3.4.0/reference/html/index.html#spring-cloud-aws-sns) 

* [Amazon SDK Docs](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-simple-notification-service.html) 

### Supported Protocol Types

- [x] Email
- [x] SMS
- [ ] SQS
- [ ] Lambda
- [ ] HTTP / HTTPS
- [ ] Email-JSON
- [ ] Platform application endpoint
- [ ] Amazon Kinesis Data Firehose

## Setup cloud environment

1. **Create an IAM Group**
   Assign permissions:

    * `AmazonEC2FullAccess`
    * `AdministratorAccess`

2. **Create a User**

    * Add the user to the group
    * Generate **Access Key ID** and **Secret Access Key**

3. **Create SNS Topic**

    * Choose `Standard` type
    * Note down the topic name for `${SNS_TOPIC}`

> [!IMPORTANT]
> After trying the demo deactivate/delete User's credentials in the AWS Management Console to prevent security risks!

---

### Application Overview

The app is configured via the `application.yml` file with environment variables for AWS credentials, region, and SNS topic details.
It runs locally on **port 5000** and disables the Spring Boot startup banner for cleaner logs.

```yaml
spring:
  application:
    name: aws-sns
  cloud:
    aws:
      region:
        static: ${AWS_REGION}
      credentials:
        access-key: ${ACCESS_KEY}
        secret-key: ${SECRET_KEY}
    sns:
      topic:
        arn: arn:aws:sns:${AWS_REGION}:${ACCOUNT_ID}:${SNS_TOPIC}
  main:
    banner-mode: off

server:
  port: 5000

logging:
  level:
    root: info
```

### Key Properties

| Property     | Description                                   | Example        |
|--------------|-----------------------------------------------|----------------|
| `AWS_REGION` | The AWS region where your SNS topic is hosted | `eu-central-1` |
| `ACCESS_KEY` | Your IAM user’s AWS access key                | `AKIA...`      |
| `SECRET_KEY` | Your IAM user’s AWS secret key                | `abcd1234...`  |
| `ACCOUNT_ID` | Your AWS account ID                           | `123456789012` |
| `SNS_TOPIC`  | The name of your SNS topic                    | `my-sns-topic` |

> [!WARNING]
> Do not hardcode credentials in your configuration file. Use environment variables or AWS Secrets Manager for better security.

---

## Running the Application

Set up environment variables (example for Linux/macOS):

```bash
export AWS_REGION=eu-central-1
export ACCESS_KEY=your-access-key
export SECRET_KEY=your-secret-key
export ACCOUNT_ID=123456789012
export SNS_TOPIC=my-sns-topic
```

Then run:

```bash
mvn spring-boot:run
```

Server starts at: `http://localhost:5000`

## Testing the Application

Subscribe email for notifications:

```bash
curl -X POST "http://localhost:5000/subscribe-email/test-email@gmail.com"
```

Send a notification to all subscribers:

```bash
curl -X POST "http://localhost:5000/send-email/HelloSubscribers"
```

Subscribe phone number for SMS notifications:

```bash
curl -X POST "http://localhost:5000/subscribe-phone/+359XXXXXXXXX"
```

Send an SMS to a specific phone number with a custom message:

```bash
curl -X POST "http://localhost:5000/sms/+359XXXXXXXXX/Hello"
```

> [!IMPORTANT]
> Subscribed emails and phone numbers must be verified manually!

### Logging

* Default log level: `info`
* Banner disabled for clean console output