package com.backend.playground.service;

import com.backend.playground.dto.UserDTO;
import com.backend.playground.entity.User;
import com.backend.playground.mapper.UserMapper;
import com.backend.playground.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserDTO userLogIn(UserDTO userDTO) {
        System.out.println(userDTO);
        User user = UserMapper.mapDtoToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user);
        return UserMapper.mapEntityToDto(userRepository.saveAndFlush(user));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
