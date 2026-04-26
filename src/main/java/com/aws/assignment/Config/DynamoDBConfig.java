package com.aws.assignment.Config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
//import com.amazonaws.auth.profile.ProfileCredentialsProvider;
//import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfig {

    private static final boolean USE_LOCAL;
    private static final String REGION;
    private static final String LOCAL_ENDPOINT;

    static {
        try {
            java.util.Properties props = new java.util.Properties();
            props.load(DynamoDBConfig.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties"));

            USE_LOCAL      = Boolean.parseBoolean(props.getProperty("aws.use-local", "false"));
            REGION         = props.getProperty("aws.region", "us-east-1");
            LOCAL_ENDPOINT = props.getProperty("aws.local-endpoint", "http://localhost:8000");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }

    @Bean
    public static AmazonDynamoDB amazonDynamoDB() {
        if (USE_LOCAL) {
            System.out.println("Connecting to LOCAL DynamoDB at " + LOCAL_ENDPOINT);
            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    LOCAL_ENDPOINT, REGION
                            )
                    )
                    .build();
        }

        System.out.println("Connecting to AWS DynamoDB in region: " + REGION);
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }
}
