package com.Ristretto.API.domain.subTask;

import com.Ristretto.API.domain.task.Task;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subtasks")
@Getter
@Setter
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
        this.isDone = false;
        this.description = data.description();
        this.dueDate = data.dueDate();
        this.task = task;
    }

    public void update(@Valid SubTaskUpdateData data) {
        if (data.title() != null) {
            this.title = data.title();
        }
        if (data.isDone() != null) {
            this.isDone = data.isDone();
        }
        if (data.description() != null) {
            this.description = data.description();
        }
        if (data.dueDate() != null) {
            this.dueDate = data.dueDate();
        }

    }
}
