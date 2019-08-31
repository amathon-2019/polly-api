package com.example.polly.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Slf4j
@Configuration
@Import(AppConfig.class)
public class AwsConfig {

    @Value("${aws.access.key}")
    private String awsAccessKey;

    @Value("${aws.secret.key}")
    private String awsSecretKey;

    @Bean
    public BasicAWSCredentials credentials() {
        log.info("]-----] AWS Configuration :: credentials [-----[ awsAccessKey : {} , awsSecretKey : {}", awsAccessKey, awsSecretKey);
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }

//    @Bean
//    public AmazonS3Client s3Client() {
//        AmazonS3Client s3Client = new AmazonS3Client(credentials());
//        s3Client.setEndpoint("s3-ap-northeast-2.amazonaws.com");
//        return s3Client;
//    }

    @Bean
    public AmazonPollyClient pollyClient() {
        AmazonPollyClient pollyClient = (AmazonPollyClient) AmazonPollyClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
        return pollyClient;
    }
}
