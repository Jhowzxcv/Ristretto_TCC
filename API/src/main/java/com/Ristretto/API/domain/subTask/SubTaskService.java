package com.Ristretto.API.domain.subTask;

import com.Ristretto.API.domain.task.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SubTaskService {

    private final SubTaskRepository repository;

    @PersistenceContext
    private EntityManager entityManager;


    public Page<SubTask> findByTaskId(Pageable pageable, Long taskId) {
        return repository.findByTaskId(taskId, pageable);
    }


    public SubTaskService(SubTaskRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SubTask convertToEntity(SubTaskRegistrationData data) {
        Task task = null;
        if (data.taskId() != null) {
            task = entityManager.getReference(Task.class, data.taskId());
        }

        SubTask subtask = new SubTask(data, task);


        return subtask;
    }


    public SubTask save(SubTask subTask) {
        return repository.save(subTask);
    }



    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
