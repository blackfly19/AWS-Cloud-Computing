package com.aws.assignment.Models;

public class LoginRequest {

    private String email;
    private String password;

    public LoginRequest() {
    }

    public String getEmail() {
        return email;
    }

    // for register page
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    // for register page
    public void setPassword(String password) {
        this.password = password;
    }
}
