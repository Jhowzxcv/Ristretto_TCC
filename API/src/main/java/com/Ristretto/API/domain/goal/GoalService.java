package com.Ristretto.API.domain.goal;

import com.Ristretto.API.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GoalService {

    private final GoalRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public GoalService(GoalRepository repository) {
        this.repository = repository;
    }

    public Page<Goal> findByUserId(Pageable pageable, Long userId) {
        return repository.findByUserId(userId, pageable);
    }

    @Transactional
    public Goal convertToEntity(GoalRegistrationData data) {
        User user = entityManager.getReference(User.class, data.userId());
        return new Goal(data, user);
    }

    public Goal findById(Long id) {
        return repository.getReferenceById(id);
    }

    public Goal save(Goal goal) {
        return repository.save(goal);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}