package com.Ristretto.API.domain.calendarEvent;

import com.Ristretto.API.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    Page<CalendarEvent> findByUserId(Long userId, Pageable pageable);
}
