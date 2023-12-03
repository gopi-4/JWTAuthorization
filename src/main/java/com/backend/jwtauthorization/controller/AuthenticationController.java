package com.backend.jwtauthorization.controller;

import com.backend.jwtauthorization.dto.AuthenticationRequestDTO;
import com.backend.jwtauthorization.dto.AuthenticationResponseDTO;
import com.backend.jwtauthorization.dto.UserDTO;
import com.backend.jwtauthorization.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody UserDTO userDTO) {
        try {
            return ResponseEntity.ok(authenticationService.register(userDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body((new AuthenticationResponseDTO()));
        }
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body((new AuthenticationResponseDTO()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponseDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        try {
            return ResponseEntity.ok(authenticationService.refreshToken(request, response));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body((new AuthenticationResponseDTO()));
        }
    }
}
