package com.aws.assignment.Controllers;

import com.aws.assignment.Models.LoginRequest;
import com.aws.assignment.Models.LoginResponse;
import com.aws.assignment.Services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return loginService.login(request);
    }
}
