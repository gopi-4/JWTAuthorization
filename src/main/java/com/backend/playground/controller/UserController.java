package com.backend.playground.controller;


import com.backend.playground.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    private ResponseEntity<String> test(){
        return ResponseEntity.ok("Success 😎");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    private ResponseEntity<String> forAdminOnly() {
        return ResponseEntity.ok("I am the Admin.");
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    private ResponseEntity<String> forUserOnly() {
        return ResponseEntity.ok("I am the User.");
    }
}
