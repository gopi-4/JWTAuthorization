package com.backend.playground.mapper;

import com.backend.playground.dto.UserDTO;
import com.backend.playground.entity.User;

public class UserMapper {

    public static User mapDtoToEntity(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setDOB(userDTO.getDOB());
        user.setRole(userDTO.getRole());
        user.setAbout(userDTO.getAbout());
        user.setPassword(userDTO.getPassword());
        user.setActive(userDTO.isActive());
        return user;
    }

    public static UserDTO mapEntityToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setDOB(user.getDOB());
        userDTO.setRole(user.getRole());
        userDTO.setAbout(user.getAbout());
        userDTO.setPassword(user.getPassword());
        userDTO.setActive(user.isActive());
        return userDTO;
    }
}
