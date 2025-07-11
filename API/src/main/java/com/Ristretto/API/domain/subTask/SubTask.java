package com.Ristretto.API.domain.subTask;

import com.Ristretto.API.domain.task.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subtasks")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "is_done")
    private Boolean isDone;

    private String description;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    public SubTask(SubTaskRegistrationData data, Task task) {
        this.title = data.title();
        this.isDone = data.isDone();
        this.description = data.description();
        this.dueDate = data.dueDate();
        this.task = task;
    }
}
