package com.backend.jwtauthorization.customs;

import com.backend.jwtauthorization.entity.User;
import com.backend.jwtauthorization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public CustomUserDetails loadUserByUsername(String username)  {
        User user = userRepository.findByEmail(username);
        if (user==null) return null;
        return new CustomUserDetails(user);
    }
}
