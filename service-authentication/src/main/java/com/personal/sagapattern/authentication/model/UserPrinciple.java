package com.personal.sagapattern.authentication.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.personal.sagapattern.application_user.ApplicationUser;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrinciple implements UserDetails {
    private static final long serialVersionUID = -4814200646262586431L;

    private UUID id;

    private String name;

    private String email;

    private String cif;

    private String username;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private UserPrinciple(UUID id, String name, String email, String cif, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cif = cif;
        this.username = email;
        this.password = password;
        this.authorities = Collections.emptyList();
    }

    public static UserPrinciple build(ApplicationUser applicationUser) {
        return new UserPrinciple(applicationUser.getId(), applicationUser.getName(), applicationUser.getEmail(),
                applicationUser.getCif(), applicationUser.getPassword());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCif() {
        return cif;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UserPrinciple user = (UserPrinciple) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
