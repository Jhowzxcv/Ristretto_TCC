package com.Ristretto.API.domain.task;

import com.Ristretto.API.domain.calendarEvent.CalendarEvent;
import com.Ristretto.API.domain.categories.Category;
import com.Ristretto.API.domain.subTask.SubTask;
import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.domain.goal.Goal;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @CreationTimestamp
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Column(name = "focus_mode")
    private Boolean focusMode;

    @Column(name = "is_done")
    private Boolean isDone;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTask> subTasks;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalendarEvent> calendarEvents;

    public Task(@Valid TaskRegistrationData data) {
        this.title = data.title();
        this.description = data.description();
        this.dueDate = data.dueDate();
        this.priority = data.priority();
        this.status = data.status();
        this.focusMode = data.focusMode();
        this.isDone = data.isDone();

        // Criando referências para os relacionamentos
        if (data.userId() != null) {
            this.user = new User();
            // Você precisará de um setter ou usar EntityManager.getReference()
        }

        if (data.categoryId() != null) {
            this.category = new Category();
            // Você precisará de um setter ou usar EntityManager.getReference()
        }

        if (data.goalId() != null) {
            this.goal = new Goal();
            // Você precisará de um setter ou usar EntityManager.getReference()
        }
    }
}