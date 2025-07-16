package com.Ristretto.API.domain.calendarEvent;

import com.Ristretto.API.domain.task.Task;
import com.Ristretto.API.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import net.fortuna.ical4j.model.property.RRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CalendarEventService {

    private final CalendarEventRepository repository;

    @PersistenceContext
    private EntityManager entityManager;


    public Page<CalendarEvent> findByUserId(Pageable pageable, Long userId) {
        return repository.findByUserId(userId, pageable);
    }


    public CalendarEventService(CalendarEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CalendarEvent convertToEntity(CalendarEventRegistrationData data) {
        if (data.startDate() != null && data.endDate() != null && data.endDate().isBefore(data.startDate())) {
            throw new IllegalArgumentException("A data de término deve ser igual ou posterior à de início");
        }

        // Validação direta da timezone
        if (data.timezone() != null && !data.timezone().isBlank()) {
            try {
                java.time.ZoneId.of(data.timezone());
            } catch (java.time.zone.ZoneRulesException e) {
                throw new IllegalArgumentException("Timezone inválida: " + data.timezone());
            }
        }

        if (data.repetitionRule() != null && !data.repetitionRule().isBlank()) {
            try {
                new RRule(data.repetitionRule()).validate();
            } catch (Exception e) {
                throw new IllegalArgumentException("Regra de repetição inválida segundo RFC 5545");
            }
        }

        User user = null;
        if (data.userId() != null) {
            user = entityManager.getReference(User.class, data.userId());
        }

        Task task = null;
        if (data.taskId() != null) {
            task = entityManager.getReference(Task.class, data.taskId());
        }

        CalendarEvent calendarEvent = new CalendarEvent(data, user, task);

        return calendarEvent;
    }




    public CalendarEvent save(CalendarEvent calendarEvent) {
        return repository.save(calendarEvent);
    }



    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
