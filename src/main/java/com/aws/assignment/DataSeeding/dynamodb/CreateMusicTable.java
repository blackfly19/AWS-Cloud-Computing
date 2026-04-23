package com.aws.assignment.DataSeeding.dynamodb;

import java.util.Arrays;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;


public class CreateMusicTable {

    public static void main(String[] args) throws Exception {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "Music";

        try {
            System.out.println("Attempting to create table; please wait...");

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(
                            new KeySchemaElement("artist", KeyType.HASH),
                            new KeySchemaElement("title_album", KeyType.RANGE)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("artist", ScalarAttributeType.S),
                            new AttributeDefinition("title_album", ScalarAttributeType.S),
                            new AttributeDefinition("year", ScalarAttributeType.S),
                            new AttributeDefinition("title", ScalarAttributeType.S)
                    )
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L))

                    // LSI: same PK (artist), different SK (year)
                    .withLocalSecondaryIndexes(
                            new LocalSecondaryIndex()
                                    .withIndexName("ArtistYearIndex")
                                    .withKeySchema(
                                            new KeySchemaElement("artist", KeyType.HASH),
                                            new KeySchemaElement("year", KeyType.RANGE)
                                    )
                                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                    )

                    // GSI: different PK (title), SK (artist)
                    .withGlobalSecondaryIndexes(
                            new GlobalSecondaryIndex()
                                    .withIndexName("TitleArtistIndex")
                                    .withKeySchema(
                                            new KeySchemaElement("title", KeyType.HASH),
                                            new KeySchemaElement("artist", KeyType.RANGE)
                                    )
                                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L))
                    );

            dynamoDB.createTable(request).waitForActive();
            System.out.println("Success. Table created.");

        } catch (Exception e) {
            System.err.println("Unable to create table:");
            System.err.println(e.getMessage());
        }
    }
}