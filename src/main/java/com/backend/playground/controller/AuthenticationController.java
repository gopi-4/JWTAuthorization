package com.backend.playground.controller;

import com.backend.playground.dto.AuthenticationRequestDTO;
import com.backend.playground.dto.AuthenticationResponseDTO;
import com.backend.playground.dto.UserDTO;
import com.backend.playground.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final Logger logger = LogManager.getLogger(AuthenticationController.class);
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody UserDTO userDTO) {
        try {
            return ResponseEntity.ok(authenticationService.register(userDTO));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body((new AuthenticationResponseDTO()));
        }
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body((new AuthenticationResponseDTO()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponseDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        try {
            return ResponseEntity.ok(authenticationService.refreshToken(request, response));
        }catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body((new AuthenticationResponseDTO()));
        }
    }

    @GetMapping("/test")
    public String getUser() {
        return "Hello";
    }
}
