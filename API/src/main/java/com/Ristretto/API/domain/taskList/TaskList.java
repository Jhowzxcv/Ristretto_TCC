package com.Ristretto.API.domain.taskList;

import com.Ristretto.API.domain.task.Task;
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
@Table(name = "task_list")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TaskList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String color;

    private String icon;

    @Enumerated(EnumType.STRING)
    private ListType type;

    // Propriedades para listas cíclicas
    @Column(name = "repetition_interval_amount")
    private Integer repetitionIntervalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "repetition_interval_type")
    private IntervalType repetitionIntervalType;

    @Column(name = "repeat_when_complete")
    private Boolean repeatWhenComplete;

    @Column(name = "manual_restart_allowed")
    private Boolean manualRestartAllowed;

    // Para listas padrão
    @Column(name = "deadline")
    private LocalDateTime deadline;

    @CreationTimestamp
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "taskList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    public TaskList(TaskListRegistrationData data, User user) {
        this.title = data.title();
        this.color = data.color();
        this.icon = data.icon();
        this.type = data.type();
        this.repetitionIntervalAmount = data.repetitionIntervalAmount();
        this.repetitionIntervalType = data.repetitionIntervalType();
        this.repeatWhenComplete = data.repeatWhenComplete();
        this.manualRestartAllowed = data.manualRestartAllowed();
        this.deadline = data.deadline();
        this.user = user;
    }

    public void update(@Valid TaskListUpdateData data) {
        if (data.title() != null) {
            this.title = data.title();
        }
        if (data.color() != null) {
            this.color = data.color();
        }
        if (data.icon() != null) {
            this.icon = data.icon();
        }
        if (data.type() != null) {
            this.type = data.type();
        }
        if (data.repetitionIntervalAmount() != null) {
            this.repetitionIntervalAmount = data.repetitionIntervalAmount();
        }
        if (data.repetitionIntervalType() != null) {
            this.repetitionIntervalType = data.repetitionIntervalType();
        }
        if (data.repeatWhenComplete() != null) {
            this.repeatWhenComplete = data.repeatWhenComplete();
        }
        if (data.manualRestartAllowed() != null) {
            this.manualRestartAllowed = data.manualRestartAllowed();
        }
        if (data.deadline() != null) {
            this.deadline = data.deadline();
        }

    }

}

