package com.backend.jwtauthorization.mapper;

import com.backend.jwtauthorization.dto.UserDTO;
import com.backend.jwtauthorization.entity.User;

public class UserMapper {

    public static User mapDtoToEntity(UserDTO userDTO) {
        return User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .DOB(userDTO.getDOB())
                .role(userDTO.getRole())
                .about(userDTO.getAbout())
                .password(userDTO.getPassword())
                .isActive(userDTO.isActive())
                .image(ImageMapper.mapDtoToEntity(userDTO.getImage()))
                .build();

    }

    public static UserDTO mapEntityToDto(User user) {
        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .DOB(user.getDOB())
                .role(user.getRole())
                .about(user.getAbout())
                .isActive(user.isActive())
                .image(ImageMapper.mapEntityToDto(user.getImage()))
                .build();
    }
}
