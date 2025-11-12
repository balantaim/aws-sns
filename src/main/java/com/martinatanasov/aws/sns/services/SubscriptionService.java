package com.martinatanasov.aws.sns.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;


@Slf4j
@Service
public class SubscriptionService implements SmsSubscription, EmailSubscription {

    private final SnsClient snsClient;
    private final String TOPIC_ARN;

    public SubscriptionService(SnsClient snsClient, @Value("${spring.cloud.sns.topic.arn}") String TOPIC_ARN) {
        this.snsClient = snsClient;
        this.TOPIC_ARN = TOPIC_ARN;
    }

    @Override
    public void subscribeEmail(final String email) {
        SubscribeResponse response = snsClient.subscribe(subscribeRequest(email, ProtocolType.EMAIL.toString()));

        log.info("Subscription request sent. Subscription ARN (if confirmed): {}", response.subscriptionArn());
        log.info("Check the email inbox to confirm the subscription.");
    }

    @Override
    public void subscribeMobileNumber(final String phone) {
        SubscribeResponse response = snsClient.subscribe(subscribeRequest(phone, ProtocolType.SMS.toString()));

        log.info("Subscription request sent for {}", phone);
        log.info("Subscription ARN (pending confirmation): {}", response.subscriptionArn());
    }

    @Override
    public boolean unsubscribe(final String endpoint) {
        try {
            // List all subscriptions for the topic
            String nextToken = null;
            do {
                ListSubscriptionsByTopicResponse response = snsClient.listSubscriptionsByTopic(ListSubscriptionsByTopicRequest.builder()
                        .topicArn(TOPIC_ARN)
                        .nextToken(nextToken)
                        .build());

                for (Subscription sub : response.subscriptions()) {
                    if (endpoint.equalsIgnoreCase(sub.endpoint())) {
                        snsClient.unsubscribe(UnsubscribeRequest.builder()
                                .subscriptionArn(sub.subscriptionArn())
                                .build());
                        log.info("Successfully unsubscribed: {}", endpoint);
                        return true;
                    }
                }
                nextToken = response.nextToken();
            } while (nextToken != null);
            log.info("No subscription found for endpoint: {}", endpoint);
            return false;
        } catch (SnsException e) {
            log.error("SNS error: {}", e.awsErrorDetails().errorMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return false;
        }
    }

    private SubscribeRequest subscribeRequest(final String subscriber, final String protocolTypeValue) {
        return SubscribeRequest.builder()
                .protocol(protocolTypeValue) // protocol can be 'email', 'sms', 'lambda', etc.
                .endpoint(subscriber)   // email address/phone number to subscribe
                .topicArn(TOPIC_ARN)
                .build();
    }

}
