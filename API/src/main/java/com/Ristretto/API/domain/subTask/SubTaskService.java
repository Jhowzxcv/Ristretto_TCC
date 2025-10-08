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
        Task task = entityManager.getReference(Task.class, data.taskId());


        return new SubTask(data, task);
    }




    public SubTask save(SubTask subTask) {
        return repository.save(subTask);
    }



    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public SubTask findByid(Long id) {
        return repository.getReferenceById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
