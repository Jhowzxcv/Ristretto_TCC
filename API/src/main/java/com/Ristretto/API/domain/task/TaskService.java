package com.Ristretto.API.domain.task;

import com.Ristretto.API.domain.taskList.TaskList;
import com.Ristretto.API.domain.category.Category;
import com.Ristretto.API.domain.goal.Goal;
import com.Ristretto.API.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Task> findAll(Pageable pageable) {
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

        TaskList taskList = null;
        if (data.taskListId() != null) {
            taskList = entityManager.getReference(TaskList.class, data.taskListId());
        }

        return new Task(data, user, category, goal, taskList);
    }

    @Transactional
    public Task updateTaskEntity(TaskUpdateData data) {
        Task existingTask = repository.getReferenceById(data.id());

        if (data.categoryId() != null) {
            Category category = entityManager.getReference(Category.class, data.categoryId());
            existingTask.setCategory(category);
        }

        if (data.goalId() != null) {
            Goal goal = entityManager.getReference(Goal.class, data.goalId());
            existingTask.setGoal(goal);
        }

        if (data.taskListId() != null) {
            TaskList taskList = entityManager.getReference(TaskList.class, data.taskListId());
            existingTask.setTaskList(taskList);
        }

        existingTask.update(data);

        return existingTask;
    }

    public Task findById(Long id) {
        return repository.getReferenceById(id);
    }

    public Task save(Task task) {
        return repository.save(task);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public Task createTask(TaskRegistrationData data) {
        Task task = convertToEntity(data);
        return save(task);
    }

    @Transactional
    public Task update(TaskUpdateData data) {
        Task updatedTask = updateTaskEntity(data);
        return save(updatedTask);
    }

    public boolean existsById(Long id) {
        return existsById(id);
    }

    public Page<Task> findAllByUserId(Long userId, Pageable pageable) {
        return repository.findAllByUserId(userId, pageable);
    }

}