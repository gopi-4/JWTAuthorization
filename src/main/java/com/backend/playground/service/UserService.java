package com.backend.playground.service;

import com.backend.playground.dto.UserDTO;
import com.backend.playground.entity.User;
import com.backend.playground.mapper.UserMapper;
import com.backend.playground.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserDTO userLogIn(UserDTO userDTO) {
        System.out.println(userDTO);
        User user = UserMapper.mapDtoToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user);
        return UserMapper.mapEntityToDto(userRepository.saveAndFlush(user));
    }
}
