package com.backend.playground.controller;


import com.backend.playground.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/test")
    private ResponseEntity<String> test(){
        System.out.println("Hii");
        return ResponseEntity.ok("Success 😎");
    }
}
