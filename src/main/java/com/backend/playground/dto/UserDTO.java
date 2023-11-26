package com.backend.playground.dto;

import com.backend.playground.enums.Role;
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

    public UserDTO(String lastName, String email, Date DOB, Role role, String about, String password, boolean isActive, ImageDTO image) {
        this.lastName = lastName;
        this.email = email;
        this.DOB = DOB;
        this.role = role;
        this.about = about;
        this.password = password;
        this.isActive = isActive;
        this.image = image;
    }
}
