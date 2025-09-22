package com.Ristretto.API.domain.taskList;

import com.Ristretto.API.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskListService {

    private final TaskListRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public TaskListService(TaskListRepository repository) {
        this.repository = repository;
    }

    public Page<TaskList> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public TaskList findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskList not found with id: " + id));
    }

    @Transactional
    public TaskList convertToEntity(TaskListRegistrationData data) {
        User user = entityManager.getReference(User.class, data.userId());
        return new TaskList(data, user);
    }

    public TaskList save(TaskList taskList) {
        return repository.save(taskList);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}