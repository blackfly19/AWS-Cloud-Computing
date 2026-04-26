package com.aws.assignment.DataSeeding.s3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;

public class UploadArtistImages {

    @Value("${aws.region}")
    private static String REGION;

    @Value("${aws.bucket-name}")
    private static String BUCKET_NAME;

    private static final String  JSON_FILE   = "2026a2_songs.json";
    private static final String  S3_PREFIX   = "artists/";

    public static void main(String[] args) throws Exception {


        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();

        JsonParser parser = new JsonFactory().createParser(new File(JSON_FILE));
        JsonNode rootNode = new ObjectMapper().readTree(parser);
        JsonNode songsNode = rootNode.path("songs");

        if (!songsNode.isArray()) {
            System.err.println("Invalid JSON format: 'songs' array not found.");
            parser.close();
            return;
        }

        // Avoid duplicate uploads when many songs share the same artist image
        Set<String> processedImageUrls = new HashSet<>();

        Iterator<JsonNode> iter = songsNode.iterator();

        while (iter.hasNext()) {
            ObjectNode currentNode = (ObjectNode) iter.next();

            String artist = currentNode.path("artist").asText();
            String imageUrl = currentNode.path("img_url").asText();

            if (artist.isEmpty() || imageUrl.isEmpty()) {
                System.err.println("Skipping invalid record: missing artist or img_url");
                continue;
            }

            if (processedImageUrls.contains(imageUrl)) {
                System.out.println("Skipping duplicate image: " + imageUrl);
                continue;
            }

            String s3Key = S3_PREFIX + makeSafeFileName(artist) + getFileExtension(imageUrl);

            File tempFile = null;

            try {
                tempFile = downloadImageToTempFile(imageUrl, artist);

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(guessContentType(imageUrl));
                metadata.setContentLength(tempFile.length());

                PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, s3Key, tempFile);
                request.setMetadata(metadata);

                s3Client.putObject(request);

                processedImageUrls.add(imageUrl);

                System.out.println("Uploaded: " + artist + " -> s3://" + BUCKET_NAME + "/" + s3Key);

            } catch (Exception e) {
                System.err.println("Failed to upload image for artist: " + artist);
                e.printStackTrace();
            } finally {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }

        parser.close();
        System.out.println("Artist image upload process completed.");
    }

    private static File downloadImageToTempFile(String imageUrl, String artist) throws Exception {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();

        File tempFile = File.createTempFile(makeSafeFileName(artist) + "_", getFileExtension(imageUrl));

        try (InputStream in = new BufferedInputStream(connection.getInputStream());
             FileOutputStream out = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }

    private static String makeSafeFileName(String input) {
        return input.toLowerCase()
                .replace("&", "and")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");
    }

    private static String getFileExtension(String url) {
        int lastDot = url.lastIndexOf('.');
        if (lastDot == -1 || lastDot < url.lastIndexOf('/')) {
            return ".jpg";
        }
        return url.substring(lastDot);
    }

    private static String guessContentType(String url) {
        String lower = url.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        return "image/jpeg";
    }
}