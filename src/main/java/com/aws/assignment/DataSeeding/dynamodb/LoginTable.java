package com.aws.assignment.DataSeeding.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoginTable {

    private static final String tableName = "login";
    private final AmazonDynamoDB dynamoDB;
    private final DynamoDB client;

    @Value("${aws.seed-data:false}")
    private boolean seedData;

    public LoginTable(AmazonDynamoDB dynamoDB, DynamoDB client) {
        this.dynamoDB = dynamoDB;
        this.client = client;
    }

    public void createTable() throws Exception {
        System.out.println("Creating table: " + tableName);
        try {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(
                            new KeySchemaElement("email", KeyType.HASH)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("email", ScalarAttributeType.S)
                    )
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L));

            dynamoDB.createTable(request);
            client.getTable(tableName).waitForActive();
            System.out.println("Table created successfully: " + tableName);

        } catch (ResourceInUseException e) {
            System.out.println("Table '" + tableName + "' already exists. Skipping creation.");
        } catch (Exception e) {
            System.err.println("Unable to create table:");
            System.err.println(e.getMessage());
        }
    }

    public void seedData() throws Exception {
        if (seedData) {
            try {
                String STUDENT_ID = "s4154781";
                String STUDENT_NAME = "Devansh";

                System.out.println("\nInserting 10 users...");

                for (int i = 0; i <= 9; i++) {
                    String email = STUDENT_ID + i + "@student.rmit.edu.au";
                    String username = STUDENT_NAME + i;
                    String password = generatePassword(i);
                    Table loginTable = client.getTable(tableName);
                    loginTable.putItem(new Item()
                            .withPrimaryKey("email", email)
                            .withString("user_name", username)
                            .withString("password", password)
                    );
                    System.out.printf("Inserted: email=%-40s user_name=%-15s password=%s%n",
                            email, username, password);
                }
                System.out.println("\nDone! 10 users inserted into '" + tableName + "' table.");

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Data already seeded.");
        }
    }

    private String generatePassword(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 6; j++) {
            sb.append((i + j) % 10);
        }
        return sb.toString();
    }
}
