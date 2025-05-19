package com.jobportal.job_portal.accounts.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Boolean isAdmin = false;

    private Boolean isSuperuser = false;

    private Boolean isBanned = false;

    private Boolean isRecruiter = false;

    @Column(length = 15)
    private String phoneNumber;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Implémentation de UserDetails pour Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isSuperuser) {
            return List.of(new SimpleGrantedAuthority("ROLE_SUPERUSER"));
        } else if (isAdmin) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (isRecruiter) {
            return List.of(new SimpleGrantedAuthority("ROLE_RECRUITER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBanned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !isBanned;
    }
}