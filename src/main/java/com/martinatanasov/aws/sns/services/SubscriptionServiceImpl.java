package com.martinatanasov.aws.sns.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;


@Slf4j
@Service
public class SubscriptionServiceImpl implements SmsSubscription, EmailSubscription {

    private final SnsClient snsClient;
    private final String TOPIC_ARN;

    public SubscriptionServiceImpl(SnsClient snsClient, @Value("${spring.cloud.sns.topic.arn}") String TOPIC_ARN) {
        this.snsClient = snsClient;
        this.TOPIC_ARN = TOPIC_ARN;
    }

    @Override
    public void subscribeEmail(final String email) {
        SubscribeRequest request = SubscribeRequest.builder()
                .protocol(ProtocolType.EMAIL.toString()) // protocol can be 'email', 'sms', 'lambda', etc.
                .endpoint(email)   // email address to subscribe
                .topicArn(TOPIC_ARN)
                .build();

        SubscribeResponse response = snsClient.subscribe(request);

        log.info("Subscription request sent. Subscription ARN (if confirmed): {}", response.subscriptionArn());
        log.info("Check the email inbox to confirm the subscription.");
    }

    @Override
    public void subscribeMobileNumber(final String phone) {
        SubscribeRequest request = SubscribeRequest.builder()
                .protocol(ProtocolType.SMS.toString()) // protocol can be 'email', 'sms', 'lambda', etc.
                .endpoint(phone)
                .topicArn(TOPIC_ARN)
                .build();

        SubscribeResponse response = snsClient.subscribe(request);

        log.info("Subscription request sent for {}", phone);
        log.info("Subscription ARN (pending confirmation): {}", response.subscriptionArn());
    }


}
