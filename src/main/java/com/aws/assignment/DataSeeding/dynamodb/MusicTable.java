package com.aws.assignment.DataSeeding.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;

@Component
public class MusicTable {

    private static final String tableName = "music";
    private final AmazonDynamoDB dynamoDB;
    private final DynamoDB client;

    @Value("${aws.seed-data:false")
    private boolean seedData;

    public MusicTable(AmazonDynamoDB dynamoDB, DynamoDB client) {
        this.dynamoDB = dynamoDB;
        this.client = client;
    }

    public void createTable() throws Exception {

        try {
            System.out.println("Attempting to create table; please wait...");

            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(
                            new KeySchemaElement("artist", KeyType.HASH),
                            new KeySchemaElement("title_album", KeyType.RANGE)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("artist", ScalarAttributeType.S),
                            new AttributeDefinition("title_album", ScalarAttributeType.S),
                            new AttributeDefinition("year", ScalarAttributeType.S),
                            new AttributeDefinition("title", ScalarAttributeType.S),
                            new AttributeDefinition("album", ScalarAttributeType.S)
                    )
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L))

                    // LSI: same PK (artist), different SK (year) and (album)
                    .withLocalSecondaryIndexes(
                            new LocalSecondaryIndex()
                                    .withIndexName("ArtistYearIndex")
                                    .withKeySchema(
                                            new KeySchemaElement("artist", KeyType.HASH),
                                            new KeySchemaElement("year", KeyType.RANGE)
                                    )
                                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL)),
                            new LocalSecondaryIndex()
                                    .withIndexName("ArtistAlbumIndex")
                                    .withKeySchema(
                                            new KeySchemaElement("artist", KeyType.HASH),
                                            new KeySchemaElement("album", KeyType.RANGE)
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

            dynamoDB.createTable(createTableRequest);
            client.getTable(tableName).waitForActive();
            System.out.println("Success. Table created.");

        } catch (ResourceInUseException e){
            System.out.println("Table '" + tableName + "' already exists. Skipping creation.");
        }
        catch (Exception e) {
            System.err.println("Unable to create table:");
            System.err.println(e.getMessage());
        }
    }

    public void seedData() throws Exception {
        if (seedData) {
            Table table = client.getTable(tableName);

            JsonParser parser = new JsonFactory().createParser(new File("2026a2_songs.json"));
            JsonNode rootNode = new ObjectMapper().readTree(parser);
            JsonNode songsNode = rootNode.path("songs");

            if (!songsNode.isArray()) {
                System.err.println("Invalid JSON format: 'songs' array not found.");
                parser.close();
                return;
            }

            Iterator<JsonNode> iter = songsNode.iterator();

            while (iter.hasNext()) {
                ObjectNode currentNode = (ObjectNode) iter.next();

                String title = currentNode.path("title").asText();
                String artist = currentNode.path("artist").asText();
                String year = currentNode.path("year").asText();
                String album = currentNode.path("album").asText();
                String imageUrl = currentNode.path("img_url").asText();

                String titleAlbum = title + "#" + album;

                try {
                    table.putItem(new Item()
                            .withPrimaryKey("artist", artist, "title_album", titleAlbum)
                            .withString("title", title)
                            .withString("artist", artist)
                            .withString("year", year)
                            .withString("album", album)
                            .withString("image_url", imageUrl));

                    System.out.println("PutItem succeeded: " + artist + " | " + titleAlbum);
                } catch (Exception e) {
                    System.err.println("Unable to add song: " + artist + " | " + titleAlbum);
                    System.err.println(e.getMessage());
                }
            }

            parser.close();
            System.out.println("Song data loaded successfully.");
        }
        else {
            System.out.println("Data already seeded.");
        }
    }
}