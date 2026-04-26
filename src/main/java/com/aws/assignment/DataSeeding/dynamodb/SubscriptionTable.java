package com.aws.assignment.DataSeeding.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionTable {

    private static final String tableName = "subscription";
    private final AmazonDynamoDB dynamoDB;
    private final DynamoDB client;

    public SubscriptionTable(AmazonDynamoDB dynamoDB, DynamoDB client) {
        this.dynamoDB = dynamoDB;
        this.client = client;
    }

    public void createTable() {
        try {
            System.out.println("Attempting to create table; please wait...");

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(
                            new KeySchemaElement("email", KeyType.HASH),
                            new KeySchemaElement("music_key", KeyType.RANGE)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("email", ScalarAttributeType.S),
                            new AttributeDefinition("music_key", ScalarAttributeType.S)
                    )
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L));

            dynamoDB.createTable(request);
            client.createTable(request).waitForActive();
            System.out.println("Success. Table created.");

        } catch (ResourceInUseException e){
            System.out.println("Table '" + tableName + "' already exists. Skipping creation.");
        }
        catch (Exception e) {
            System.err.println("Unable to create table:");
            System.err.println(e.getMessage());
        }
    }
}