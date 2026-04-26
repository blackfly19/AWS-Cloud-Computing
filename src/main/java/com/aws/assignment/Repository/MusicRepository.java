package com.aws.assignment.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MusicRepository {

    private final AmazonDynamoDB dynamoDB;

    public MusicRepository(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    public void AddSong(){
        // Function
    }
}
