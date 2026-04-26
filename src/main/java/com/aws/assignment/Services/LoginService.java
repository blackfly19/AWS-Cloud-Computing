package com.aws.assignment.Services;

import com.aws.assignment.Models.LoginRequest;
import com.aws.assignment.Models.LoginResponse;
import com.aws.assignment.Models.LoginUser;
import com.aws.assignment.Repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private static final String INVALID_MESSAGE = "email or password is invalid";

    private final LoginRepository loginRepository;

    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            return LoginResponse.failure(INVALID_MESSAGE);
        }

        LoginUser user = loginRepository.findByEmail(request.getEmail());

        if (user == null) {
            return LoginResponse.failure(INVALID_MESSAGE);
        }

        if (!request.getPassword().equals(user.getPassword())) {
            return LoginResponse.failure(INVALID_MESSAGE);
        }

        return LoginResponse.success(user.getEmail(), user.getUserName());
    }
}
