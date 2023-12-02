package com.backend.playground.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oAuth2")
public class OAuthController {

    @GetMapping("/user")
    public String getUser(HttpServletRequest request) {
        System.out.println(request.getHeader("Authorization"));
        return "Inside getUser";
    }

    @GetMapping("/test")
    public String getUser() {
        return "Hello";
    }
}
