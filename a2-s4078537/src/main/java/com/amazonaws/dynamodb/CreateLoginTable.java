package com.amazonaws.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

public class CreateLoginTable {

    private static final String STUDENT_ID = "s4078537";
    private static final Regions REGION = Regions.US_EAST_1;

    public static void main(String[] args) throws InterruptedException {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        String tableName = "login";

        // Create the table
        System.out.println("Creating table: " + tableName);
        try {
            Table table = dynamoDB.createTable(
                    tableName,
                    java.util.Arrays.asList(
                            new KeySchemaElement("email", KeyType.HASH)
                    ),
                    java.util.Arrays.asList(
                            new AttributeDefinition("email", ScalarAttributeType.S)
                    ),
                    new ProvisionedThroughput(5L, 5L)
            );
            System.out.println("Waiting for table to become ACTIVE...");
            table.waitForActive();
            System.out.println("Table created successfully: " + table.getTableName());

        } catch (ResourceInUseException e) {
            System.out.println("Table '" + tableName + "' already exists. Skipping creation.");
        }

        // Insert 10 users
        Table loginTable = dynamoDB.getTable(tableName);
        System.out.println("\nInserting 10 users...");

        for (int i = 0; i <= 9; i++) {
            String email = STUDENT_ID + i + "@student.rmit.edu.au";
            String username = "PinYuHuang" + i;
            String password = generatePassword(i);

            loginTable.putItem(new Item()
                    .withPrimaryKey("email", email)
                    .withString("user_name", username)
                    .withString("password", password)
            );
            System.out.printf("Inserted: email=%-40s user_name=%-15s password=%s%n",
                    email, username, password);
        }
        System.out.println("\nDone! 10 users inserted into '" + tableName + "' table.");
    }

    private static String generatePassword(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 6; j++) {
            sb.append((i + j) % 10);
        }
        return sb.toString();
    }
}