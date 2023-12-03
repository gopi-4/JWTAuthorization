package com.backend.jwtauthorization.dto;

import com.backend.jwtauthorization.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String firstName;
    private String lastName;
    private String email;
    private Date DOB;
    private Role role;
    private String about;
    private String password;
    private boolean isActive;
    private ImageDTO image;
}
