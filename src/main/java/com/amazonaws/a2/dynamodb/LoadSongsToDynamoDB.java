package com.amazonaws.a2.dynamodb;

import java.io.File;
import java.util.Iterator;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LoadSongsToDynamoDB {

    public static void main(String[] args) throws Exception {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("Music");

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
}