package com.amazonaws.webapp;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

public class RegisterService {

    private static final Regions REGION     = Regions.US_EAST_1;
    private static final String  TABLE_NAME = "login";

    private final DynamoDB dynamoDB;
    private final Table    loginTable;

    public RegisterService() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();
        this.dynamoDB   = new DynamoDB(client);
        this.loginTable = dynamoDB.getTable(TABLE_NAME);
    }

    // Returns null if success, error message if failed
    public String register(String email, String username, String password) {
        // Check if email already exists
        Item existing = loginTable.getItem(new GetItemSpec()
                .withPrimaryKey("email", email));

        if (existing != null) {
            return "The email already exists";
        }

        // Insert new user
        loginTable.putItem(new Item()
                .withPrimaryKey("email", email)
                .withString("user_name", username)
                .withString("password", password)
        );

        return null; // success
    }
}