package com.Events.Tickets.infraestructura.security;

import com.Events.Tickets.dominio.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Clase Adaptadora
public class CustomUserDetails implements UserDetails {

    private final User domainUser;

    public CustomUserDetails(User domainUser) {
        this.domainUser = domainUser;
    }

    // Mapeamos el rol del Dominio a la autoridad de Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + domainUser.getRole().name()));
    }

    @Override
    public String getPassword() {
        // Devuelve la contraseña cifrada almacenada en el objeto de Dominio
        return domainUser.getPassword();
    }

    @Override
    public String getUsername() {
        return domainUser.getUsername();
    }

    // ----------------------------------------------------
    // Métodos que deben retornar 'true' por defecto en un sistema simple
    // ----------------------------------------------------
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