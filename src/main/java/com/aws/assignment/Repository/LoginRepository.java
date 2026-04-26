package com.aws.assignment.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.aws.assignment.Models.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class LoginRepository {

    private static final String TABLE_NAME = "login";

    private final AmazonDynamoDB amazonDynamoDB;

    public LoginRepository(AmazonDynamoDB amazonDynamoDB) {
        this.amazonDynamoDB = amazonDynamoDB;
    }

    public LoginUser findByEmail(String email) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", new AttributeValue().withS(email));

        GetItemRequest request = new GetItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(key);

        GetItemResult result = amazonDynamoDB.getItem(request);

        if (result.getItem() == null || result.getItem().isEmpty()) {
            return null;
        }

        Map<String, AttributeValue> item = result.getItem();

        LoginUser user = new LoginUser();
        user.setEmail(getStringValue(item, "email"));
        user.setUserName(getStringValue(item, "user_name"));
        user.setPassword(getStringValue(item, "password"));

        return user;
    }

    private String getStringValue(Map<String, AttributeValue> item, String attributeName) {
        AttributeValue value = item.get(attributeName);
        return value == null ? null : value.getS();
    }
}
