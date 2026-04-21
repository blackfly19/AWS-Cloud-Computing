package com.amazonaws.a2.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class LoadLoginData {

    public static void main(String[] args) throws Exception {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("Login");

        try {
            putUser(table, "s340880680@student.rmit.edu.au", "RemyMartin0", "012345");
            putUser(table, "s340880681@student.rmit.edu.au", "RemyMartin1", "123456");
            putUser(table, "s340880682@student.rmit.edu.au", "RemyMartin2", "234560");
            putUser(table, "s340880683@student.rmit.edu.au", "RemyMartin3", "345601");
            putUser(table, "s340880684@student.rmit.edu.au", "RemyMartin4", "456012");
            putUser(table, "s340880685@student.rmit.edu.au", "RemyMartin5", "560123");
            putUser(table, "s340880686@student.rmit.edu.au", "RemyMartin6", "601234");
            putUser(table, "s340880687@student.rmit.edu.au", "RemyMartin7", "345678");
            putUser(table, "s340880688@student.rmit.edu.au", "RemyMartin8", "456789");
            putUser(table, "s34088069@student.rmit.edu.au", "RemyMartin9", "901234");

            System.out.println("Login data loaded successfully.");
        } catch (Exception e) {
            System.err.println("Unable to load login data.");
            System.err.println(e.getMessage());
        }
    }

    private static void putUser(Table table, String email, String userName, String password) {
        table.putItem(new Item()
                .withPrimaryKey("email", email)
                .withString("user_name", userName)
                .withString("password", password));

        System.out.println("Inserted user: " + email);
    }
}