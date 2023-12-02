package com.backend.playground.entity;

import com.backend.playground.customs.CustomUserDetails;
import com.backend.playground.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id;
    private String firstName;
    private String lastName;
    private String email;
    private Date DOB;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String about;
    private String password;
    private boolean isActive;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "image_id")
    private Image image;

    public User(@NonNull CustomUserDetails customUserDetails) {
        this.Id=customUserDetails.getId();
        this.firstName=customUserDetails.getFirstName();
        this.lastName=customUserDetails.getLastName();
        this.email=customUserDetails.getEmail();
        this.DOB=customUserDetails.getDOB();
        this.role=customUserDetails.getRole();
        this.about=customUserDetails.getAbout();
        this.password=customUserDetails.getPassword();
        this.isActive= customUserDetails.isActive();
        this.image=customUserDetails.getImage();
    }
}
