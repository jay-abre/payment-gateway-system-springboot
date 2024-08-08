package com.electric_titans.accountservice.entity;

import com.electric_titans.accountservice.security.CustomUserDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Data
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements CustomUserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "is_blacklisted", nullable = false)
    private boolean blacklisted;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.debug("getAuthorities()");
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName().toString());
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        log.debug("isAccountNonExpired()");
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        log.debug("isAccountNonLocked()");
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        log.debug("isCredentialsNonExpired()");
        return true;
    }

    @Override
    public boolean isEnabled() {
        log.debug("isEnabled()");
        return true;
    }
}