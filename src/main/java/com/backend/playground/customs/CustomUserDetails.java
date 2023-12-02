package com.backend.playground.customs;

import com.backend.playground.entity.Image;
import com.backend.playground.entity.User;
import com.backend.playground.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

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
    private Image image;

    private User user;

    public CustomUserDetails(User user) {
        this.Id=user.getId();
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.email=user.getEmail();
        this.DOB=user.getDOB();
        this.role=user.getRole();
        this.about=user.getAbout();
        this.password=user.getPassword();
        this.isActive= user.isActive();
        this.image=user.getImage();
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
