package com.Ristretto.API.domain.task;

import com.Ristretto.API.domain.categories.Category;
import com.Ristretto.API.domain.goal.Goal;
import com.Ristretto.API.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository Repository;

    @PersistenceContext
    private EntityManager entityManager;

    public TaskService(TaskRepository Repository) {
        this.Repository = Repository;
    }

    public Page<Task> findAll(Pageable pageable){
        return Repository.findAll(pageable);
    }

    @Transactional
    public Task register(TaskRegistrationData data) {
        User user = null;
        if (data.userId() != null) {
            user = entityManager.getReference(User.class, data.userId());
        }

        Category category = null;
        if (data.categoryId() != null) {
            category = entityManager.getReference(Category.class, data.categoryId());
        }

        Goal goal = null;
        if (data.goalId() != null) {
            goal = entityManager.getReference(Goal.class, data.goalId());
        }

        Task task = new Task(data, user, category, goal);

        return Repository.save(task);
    }

    public void deleteById(Long id) {
        Repository.deleteById(id);
    }
}
