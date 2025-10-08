package com.Ristretto.API.domain.taskList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    Page<TaskList> findAllByUserId(Pageable pageable, Long userId);
}
