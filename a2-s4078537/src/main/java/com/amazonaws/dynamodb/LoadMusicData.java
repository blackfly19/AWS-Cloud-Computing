package com.amazonaws.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;
import java.util.Map;

public class LoadMusicData {

    private static final Regions REGION    = Regions.US_EAST_1;
    private static final String  JSON_FILE = "2026a2_songs.json";

    public static void main(String[] args) throws Exception {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        Table musicTable  = dynamoDB.getTable("music");

        // Parse JSON file
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(JSON_FILE);

        if (!jsonFile.exists()) {
            System.err.println("ERROR: Cannot find '" + JSON_FILE + "'");
            System.err.println("Place it in the project root directory.");
            System.exit(1);
        }

        Map<String, List<Map<String, String>>> root =
                mapper.readValue(jsonFile,
                        new TypeReference<Map<String, List<Map<String, String>>>>() {});

        List<Map<String, String>> songs = root.get("songs");
        System.out.println("Found " + songs.size() + " songs in " + JSON_FILE);

        int loaded  = 0;
        int failed  = 0;

        for (Map<String, String> song : songs) {
            String title    = song.get("title");
            String artist   = song.get("artist");
            String year     = song.get("year");
            String album    = song.get("album");
            String imageUrl = song.get("img_url");

            // Composite SK: title#album ensures no overwrites
            // e.g. same title on different albums stays as separate items
            String sortKey  = title + "#" + album;

            try {
                musicTable.putItem(new Item()
                        .withPrimaryKey(
                                "artist",      artist,
                                "title#album", sortKey
                        )
                        .withString("title",     title)
                        .withString("year",      year)
                        .withString("album",     album)
                        .withString("image_url", imageUrl)
                );
                System.out.printf("Loaded: %-45s | artist: %-35s | year: %s%n",
                        title, artist, year);
                loaded++;

            } catch (Exception e) {
                System.err.printf("FAILED: %-45s | %s%n", title, e.getMessage());
                failed++;
            }
        }

        System.out.println("\n── Summary ──────────────────────────────────────");
        System.out.println("  Loaded : " + loaded);
        System.out.println("  Failed : " + failed);
        System.out.println("  Total  : " + songs.size());
        System.out.println("─────────────────────────────────────────────────");
    }
}