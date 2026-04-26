package com.aws.assignment.Config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfig {

    @Value("${aws.use-local:false}")
    private boolean useLocal;

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.local-endpoint:http://localhost:8000}")
    private String localEndpoint;

    @Value("${aws.accessKeyId:dummy}")
    private String accessKeyId;

    @Value("${aws.secretKey:dummy}")
    private String secretKey;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        if (useLocal) {
            System.out.println("Connecting to LOCAL DynamoDB at " + localEndpoint);
            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(localEndpoint, region)
                    )
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(accessKeyId, secretKey)
                    ))
                    .build();
        }
        System.out.println("Connecting to AWS DynamoDB in region: " + region);
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB client) {
        return new DynamoDB(client);
    }
}
