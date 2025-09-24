package com.Ristretto.API.domain.user;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    @Column(name = "default_reminder_minutes")
    private Integer defaultReminderMin;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User(@Valid UserRegistrationData data) {
        this.name = data.name();
        this.email = data.email();
        this.passwordHash = data.password();
        this.theme = data.theme();
        this.defaultReminderMin = data.defaultReminderMin();
        this.avatarUrl = data.avatarUrl();
    }

    public User(String name, String email, String passwordHash, Theme theme,
                Integer defaultReminderMin, String avatarUrl) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash; // ← Recebe já criptografada
        this.theme = theme;
        this.defaultReminderMin = defaultReminderMin;
        this.avatarUrl = avatarUrl;
    }

    // ============ MÉTODOS DO UserDetails ============

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email; // Usa email como username
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

    public void update(UserUpdateData data) {
        if (data.name() != null) {
            this.name = data.name();
        }
        if (data.theme() != null) {
            this.theme = data.theme();
        }
        if (data.defaultReminderMin() != null) {
            this.defaultReminderMin = data.defaultReminderMin();
        }
        if (data.avatarUrl() != null) {
            this.avatarUrl = data.avatarUrl();
        }

    }
}