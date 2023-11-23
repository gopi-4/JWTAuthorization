package com.backend.playground.controller;

import com.backend.playground.dto.UserDTO;
import com.backend.playground.entity.User;
import com.backend.playground.service.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/user")
public class UserController {

    @PostMapping("/login")
    private Mono<User> userLogIn(@RequestBody UserDTO userDTO) {
        return Mono.just(UserService.userLogIn(userDTO));
    }
}
