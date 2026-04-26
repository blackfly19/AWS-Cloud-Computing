package com.aws.assignment.DataSeeding;

import com.aws.assignment.DataSeeding.dynamodb.LoginTable;
import com.aws.assignment.DataSeeding.dynamodb.MusicTable;
import com.aws.assignment.DataSeeding.dynamodb.SubscriptionTable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSetup implements ApplicationRunner {

    private final LoginTable loginTable;
    private final MusicTable musicTable;
    private final SubscriptionTable subscriptionTable;

    public DatabaseSetup(LoginTable loginTable, MusicTable musicTable, SubscriptionTable subscriptionTable) {
        this.loginTable = loginTable;
        this.musicTable = musicTable;
        this.subscriptionTable = subscriptionTable;
    }

    public void run(ApplicationArguments args) throws Exception {
        try {
            //Login table
            loginTable.createTable();
            loginTable.seedData();

            // Music table
            musicTable.createTable();
            musicTable.seedData();

            //Subscription table
            subscriptionTable.createTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
