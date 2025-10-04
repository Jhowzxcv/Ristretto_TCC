package com.Ristretto.API.domain.calendarEvent;

import com.Ristretto.API.domain.task.Task;
import com.Ristretto.API.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CalendarEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    private String timezone;

    private String color;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private Boolean repetitive;

    @Enumerated(EnumType.STRING)
    @Column(name = "repetition_type")
    private RepetitionType repetitionType;

    @Column(name = "repetition_rule")
    private String  repetitionRule;

    @Column(name = "tactile_reminder")
    private Boolean tactileReminder;


    public CalendarEvent(CalendarEventRegistrationData data, User user, Task task) {
        this.title = data.title();
        this.description = data.description();
        this.startDate = data.startDate();
        this.endDate = data.endDate();
        this.timezone = data.timezone();
        this.color = data.color();
        this.repetitive = data.repetitive();
        this.repetitionType = data.repetitionType();
        this.repetitionRule = data.repetitionRule();
        this.tactileReminder = data.tactileReminder();
        this.user = user;
        this.task = task;
    }

    public void update(@Valid CalendarEventUpdateData data) {
        if (data.title() != null) {
            this.title = data.title();
        }
        if (data.description() != null) {
            this.description = data.description();
        }
        if (data.startDate() != null) {
            this.startDate = data.startDate();
        }
        if (data.endDate() != null) {
            this.endDate = data.endDate();
        }
        if (data.timezone() != null) {
            this.timezone = data.timezone();
        }
        if (data.color() != null) {
            this.color = data.color();
        }
        if (data.repetitive() != null) {
            this.repetitive = data.repetitive();
        }
        if (data.repetitionType() != null) {
            this.repetitionType = data.repetitionType();
        }
        if (data.repeitionRule() != null) {
            this.repetitionRule = data.repeitionRule();
        }
        if (data.tactileReminder() != null) {
            this.tactileReminder = data.tactileReminder();
        }
    }
}
