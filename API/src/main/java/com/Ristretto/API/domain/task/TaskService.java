package com.Ristretto.API.domain.task;

import com.Ristretto.API.domain.taskList.TaskList;
import com.Ristretto.API.domain.categories.Category;
import com.Ristretto.API.domain.goal.Goal;
import com.Ristretto.API.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {


    @Autowired
    private TaskRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Task> findAll(Pageable pageable){
        return repository.findAll(pageable);
    }

    @Transactional
    public Task convertToEntity(TaskRegistrationData data) {
        User user = entityManager.getReference(User.class, data.userId());

        Category category = null;
        if (data.categoryId() != null) {
            category = entityManager.getReference(Category.class, data.categoryId());
        }

        Goal goal = null;
        if (data.goalId() != null) {
            goal = entityManager.getReference(Goal.class, data.goalId());
        }

        TaskList list = null;
        if (data.taskListId() != null){
            list = entityManager.getReference(TaskList.class, data.taskListId());
        }



        return new Task(data, user, category, goal, list);
    }

    public Task save(Task task) {
        return repository.save(task);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
