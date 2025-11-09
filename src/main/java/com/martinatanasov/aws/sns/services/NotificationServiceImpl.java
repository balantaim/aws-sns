package com.martinatanasov.aws.sns.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Service
public class NotificationServiceImpl implements EmailNotification, SmsNotification {

    /**
     * Use Async SnsClient instead of SnsTemplate in order to reduce the jar size
     */

    // private final SnsTemplate snsTemplate;
    private final SnsClient snsClient;
    private final String TOPIC_ARN;

    public NotificationServiceImpl(SnsClient snsClient,
                                   // SnsTemplate snsTemplate,
                                   @Value("${spring.cloud.sns.topic.arn}") String TOPIC_ARN) {
        // this.snsTemplate = snsTemplate;
        this.snsClient = snsClient;
        this.TOPIC_ARN = TOPIC_ARN;
    }

    @Override
    public void sendEmailNotification(final String message) {
        snsClient.publish(request -> request
                .topicArn(TOPIC_ARN)
                .message(message)
                .subject("Test notification"));
    }

    @Override
    public void sendSms(final String phoneNumber, final String message) {
        PublishRequest request = PublishRequest.builder()
                .message(message)
                .phoneNumber(phoneNumber)
                .build();

        PublishResponse response = snsClient.publish(request);
        log.info("SMS sent! Message ID: {}", response.messageId());
    }

    /*
    public void sendNotification(final String message) {
        // Use pattern "topic-arn", "payload", "subject"
        snsTemplate.sendNotification(TOPIC_ARN, message, "test subject");
    }
     */

}
