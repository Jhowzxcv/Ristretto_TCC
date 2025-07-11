package com.Ristretto.API.domain.subTask;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Stream;

public interface SubTaskRepository extends JpaRepository<SubTask ,Long> {
    Page<SubTask> findByTaskId(Long taskId, Pageable pageable);

}
