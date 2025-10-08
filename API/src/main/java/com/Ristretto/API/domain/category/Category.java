package com.Ristretto.API.domain.category;

import com.Ristretto.API.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@Setter
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
        this.user = user;
        this.icon = (data.icon() != null && data.icon().isEmpty()) ? null : data.icon();
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
