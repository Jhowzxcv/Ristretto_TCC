package com.Ristretto.API.domain.task;

import com.Ristretto.API.domain.taskList.TaskList;
import com.Ristretto.API.domain.calendarEvent.CalendarEvent;
import com.Ristretto.API.domain.category.Category;
import com.Ristretto.API.domain.goal.Goal;
import com.Ristretto.API.domain.subTask.SubTask;
import com.Ristretto.API.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "task_list_id")
    private TaskList taskList;

    @Column(name = "focus_mode")
    private Boolean focusMode;

    @Column(name = "is_done")
    private Boolean isDone;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTask> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalendarEvent> calendarEvents = new ArrayList<>();

    // Construtor para TaskRegistrationData (já existe)
    public Task(TaskRegistrationData data, User user, Category category, Goal goal, TaskList taskList) {
        this.title = data.title();
        this.description = data.description();
        this.dueDate = data.dueDate();
        this.priority = data.priority();
        this.status = data.status();
        this.focusMode = data.focusMode();
        this.isDone = data.isDone();
        this.user = user;
        this.category = category;
        this.goal = goal;
        this.taskList = taskList;
    }

    // Método corrigido para atualização
    public void update(@Valid TaskUpdateData data) {
        if (data.title() != null) {
            this.title = data.title();
        }
        if (data.description() != null) {
            this.description = data.description();
        }
        if (data.dueDate() != null) {
            this.dueDate = data.dueDate();
        }
        if (data.priority() != null) {
            this.priority = data.priority();
        }
        if (data.status() != null) {
            this.status = data.status();
        }
        if (data.focusMode() != null) {
            this.focusMode = data.focusMode();
        }
        // categoryId, goalId, taskListId são tratados no service
        // pois precisam ser convertidos para entidades
    }
}