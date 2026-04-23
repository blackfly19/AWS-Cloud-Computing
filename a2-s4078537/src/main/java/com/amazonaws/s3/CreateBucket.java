package com.amazonaws.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;

public class CreateBucket {

    private static final Regions REGION = Regions.US_EAST_1;
    private static final String  BUCKET_NAME = "s4078537-music-images"; // replace with your bucket name

    public static void main(String[] args) {

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(REGION)
                    .build();

            if (!s3Client.doesBucketExistV2(BUCKET_NAME)) {
                s3Client.createBucket(new CreateBucketRequest(BUCKET_NAME));
                String bucketLocation = s3Client.getBucketLocation(
                        new GetBucketLocationRequest(BUCKET_NAME));
                System.out.println("Bucket created! Location: " + bucketLocation);
            } else {
                System.out.println("Bucket already exists: " + BUCKET_NAME);
            }

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }
}