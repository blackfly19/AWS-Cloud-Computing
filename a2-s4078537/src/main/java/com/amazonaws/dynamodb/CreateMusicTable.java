package com.amazonaws.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

public class CreateMusicTable {

    private static final Regions REGION = Regions.US_EAST_1;

    public static void main(String[] args) throws InterruptedException {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        String tableName = "music";

        System.out.println("Creating table: " + tableName);

        try {
            Table table = dynamoDB.createTable(new CreateTableRequest()
                    .withTableName(tableName)
                    // Main table: PK=artist, SK=title#album
                    .withKeySchema(
                            new KeySchemaElement("artist",      KeyType.HASH),
                            new KeySchemaElement("title#album", KeyType.RANGE)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("artist",      ScalarAttributeType.S),
                            new AttributeDefinition("title#album", ScalarAttributeType.S),
                            new AttributeDefinition("year",        ScalarAttributeType.S),
                            new AttributeDefinition("title",       ScalarAttributeType.S)
                    )
                    // LSI: same PK (artist), SK=year
                    // Use case: find all songs by an artist in a specific year
                    .withLocalSecondaryIndexes(
                            new LocalSecondaryIndex()
                                    .withIndexName("artist-year-index")
                                    .withKeySchema(
                                            new KeySchemaElement("artist", KeyType.HASH),
                                            new KeySchemaElement("year",   KeyType.RANGE)
                                    )
                                    .withProjection(new Projection()
                                            .withProjectionType(ProjectionType.ALL))
                    )
                    // GSI: PK=title, SK=artist
                    // Use case: find all artists that have a song with a given title
                    .withGlobalSecondaryIndexes(
                            new GlobalSecondaryIndex()
                                    .withIndexName("title-artist-index")
                                    .withKeySchema(
                                            new KeySchemaElement("title",  KeyType.HASH),
                                            new KeySchemaElement("artist", KeyType.RANGE)
                                    )
                                    .withProjection(new Projection()
                                            .withProjectionType(ProjectionType.ALL))
                                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L))
                    )
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L))
            );

            System.out.println("Waiting for table to become ACTIVE...");
            table.waitForActive();
            System.out.println("Table created successfully: " + table.getTableName());
            System.out.println("  Main table  PK: artist | SK: title#album");
            System.out.println("  LSI  artist-year-index  PK: artist | SK: year");
            System.out.println("  GSI  title-artist-index PK: title  | SK: artist");

        } catch (ResourceInUseException e) {
            System.out.println("Table '" + tableName + "' already exists. No action taken.");
        }
    }
}