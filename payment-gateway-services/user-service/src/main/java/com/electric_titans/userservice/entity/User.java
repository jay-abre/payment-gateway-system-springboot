package com.electric_titans.userservice.entity;

import com.electric_titans.userservice.enums.UserStatusEnum;
import com.electric_titans.userservice.security.CustomUserDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
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

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "mobile_number", length = 20, nullable = false)
    private String mobileNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "is_blacklisted", nullable = false)
    private boolean blacklisted;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatusEnum status;

    @Column(name = "customer_id")
    private String customerId;

    @OneToOne(mappedBy = "user")
    private UserProfile userProfile;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.debug("getAuthorities()");
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName().toString());
        return List.of(authority);
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