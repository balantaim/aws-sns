package com.martinatanasov.aws.sns.config;

import io.awspring.cloud.autoconfigure.sns.SnsClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.time.Duration;

@Configuration
public class SnsClientConfiguration {

    @Bean
    SnsClientCustomizer customizer(@Value("${spring.cloud.aws.region.static}") String region,
                                   final AwsCredentialsProvider credentialsProvider) {
        return builder -> {
            builder
                    .region(Region.of(region))
                    .credentialsProvider(credentialsProvider)
                    .overrideConfiguration(config ->
                            config.apiCallTimeout(Duration.ofMillis(1500))
                    );
        };
    }

}
