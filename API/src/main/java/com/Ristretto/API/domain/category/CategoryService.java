package com.Ristretto.API.domain.category;

import com.Ristretto.API.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    @PersistenceContext
    private EntityManager entityManager;


    public Page<Category> findByUserId(Pageable pageable, Long userId) {
        return repository.findByUserId(userId, pageable);
    }


    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Category convertToEntity(CategoryRegistrationData data) {
        User user = entityManager.getReference(User.class, data.userId());




        return new Category(data, user);
    }


    public Category save(Category category) {
        return repository.save(category);
    }

    public Category findByid(Long id) {
        return repository.getReferenceById(id);
    }


    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
