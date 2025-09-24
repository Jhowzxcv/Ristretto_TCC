package com.Ristretto.API.domain.category;

import com.Ristretto.API.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String color;
    private String icon;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Category(CategoryRegistrationData data, User user) {
        this.name = data.name();
        this.description = data.description();
        this.color = data.color();
        this.icon = data.icon();
        this.user = user;
    }

    public void update(@Valid CategoryUpdateData data) {
        if (data.name() != null) {
            this.name = data.name();
        }
        if (data.description() != null) {
            this.description = data.description();
        }
        if (data.color() != null) {
            this.color = data.color();
        }
        if (data.icon() != null) {
            this.icon = data.icon();
        }
    }
}
