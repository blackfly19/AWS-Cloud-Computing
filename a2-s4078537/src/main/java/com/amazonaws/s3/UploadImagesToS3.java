package com.amazonaws.s3;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.util.*;

public class UploadImagesToS3 {

    private static final Regions REGION      = Regions.US_EAST_1;
    private static final String  BUCKET_NAME = "s4078537-music-images";
    private static final String  JSON_FILE   = "2026a2_songs.json";
    private static final String  S3_PREFIX   = "images/";

    public static void main(String[] args) throws Exception {

        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();

        // Parse JSON and collect unique image URLs
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

        // Deduplicate image URLs
        Set<String> uniqueUrls = new LinkedHashSet<>();
        for (Map<String, String> song : songs) {
            String imgUrl = song.get("img_url");
            if (imgUrl != null && !imgUrl.isEmpty()) {
                uniqueUrls.add(imgUrl);
            }
        }

        System.out.println("Found " + uniqueUrls.size() + " unique images to upload.");
        System.out.println("Target bucket: " + BUCKET_NAME + "\n");

        int uploaded = 0;
        int failed   = 0;

        for (String imgUrl : uniqueUrls) {
            String filename = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
            String s3Key    = S3_PREFIX + filename;
            File localFile  = new File(filename);

            try {
                // Step 1: Download image to local file
                try (InputStream in = new URL(imgUrl).openStream();
                     FileOutputStream out = new FileOutputStream(localFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }

                // Step 2: Upload local file to S3
                s3.putObject(BUCKET_NAME, s3Key, localFile);

                System.out.printf("Uploaded: %-30s → s3://%s/%s%n",
                        filename, BUCKET_NAME, s3Key);
                uploaded++;

            } catch (Exception e) {
                System.err.printf("FAILED : %-30s | %s%n", filename, e.getMessage());
                failed++;

            } finally {
                // Step 3: Delete local file
                if (localFile.exists()) {
                    localFile.delete();
                }
            }
        }

        System.out.println("\n── Summary ──────────────────────────────────────");
        System.out.println("  Uploaded : " + uploaded);
        System.out.println("  Failed   : " + failed);
        System.out.println("  Total    : " + uniqueUrls.size());
        System.out.println("─────────────────────────────────────────────────");
    }
}