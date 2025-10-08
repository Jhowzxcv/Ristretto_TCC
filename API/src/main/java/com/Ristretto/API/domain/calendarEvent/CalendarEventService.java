package com.Ristretto.API.domain.calendarEvent;

import com.Ristretto.API.domain.category.Category;
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
        User user = entityManager.getReference(User.class, data.userId());

        Task task = null;
        if (data.taskId() != null) {
            task = entityManager.getReference(Task.class, data.taskId());
        }

        return new CalendarEvent(data, user, task);
    }

    public CalendarEvent findByid(Long id) {
        return repository.getReferenceById(id);
    }



    public CalendarEvent save(CalendarEvent calendarEvent) {
        return repository.save(calendarEvent);
    }



    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
