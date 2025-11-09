package com.martinatanasov.aws.sns.config;

import io.awspring.cloud.sns.core.TopicArnResolver;
import io.awspring.cloud.sns.core.TopicsListingTopicArnResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class TopicArnResolverConfiguration {

    @Bean
    public TopicArnResolver topicArnResolver(SnsClient snsClient) {
        return new TopicsListingTopicArnResolver(snsClient);
    }

}
