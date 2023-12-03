package com.backend.jwtauthorization.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/user")
public class UserController {

    @GetMapping("/test")
    private ResponseEntity<String> test(){
        return ResponseEntity.ok("Success 😎");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    private ResponseEntity<String> forAdminOnly() {
        return ResponseEntity.ok("I am the Admin.");
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    private ResponseEntity<String> forUserOnly() {
        return ResponseEntity.ok("I am the User.");
    }
}
