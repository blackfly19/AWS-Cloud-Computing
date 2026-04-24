package com.aws.assignment.Models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private boolean success;
    private String email;

    @JsonProperty("user_name")
    private String userName;

    private String message;

    public LoginResponse() {
    }

    public static LoginResponse success(String email, String userName) {
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setEmail(email);
        response.setUserName(userName);
        return response;
    }

    public static LoginResponse failure(String message) {
        LoginResponse response = new LoginResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    @JsonProperty("user_name")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
