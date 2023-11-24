package com.backend.playground.service;

import com.backend.playground.dto.UserDTO;
import com.backend.playground.entity.User;
import com.backend.playground.mapper.UserMapper;
import com.backend.playground.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User userLogIn(UserDTO userDTO) {
//        System.out.println(userDTO);
        User user = UserMapper.mapDtoToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        System.out.println(user);
        return user;
    }
}
