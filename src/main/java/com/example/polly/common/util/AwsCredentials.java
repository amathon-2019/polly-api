package com.example.polly.common.util;

import com.amazonaws.auth.BasicAWSCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Slf4j
public class AwsCredentials {
    @Value("${aws.access.key}")
    private String awsAccessKey;

    @Value("${aws.secret.key}")
    private String awsSecretKey;

    @Bean
    public BasicAWSCredentials credentials() {
        log.info("]-----] AwsCredentials.credentials [-----[ awsAccessKey : {} , awsSecretKey : {}", awsAccessKey, awsSecretKey);
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }
}
