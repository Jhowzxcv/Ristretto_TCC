package com.Ristretto.API.domain.categories;

import com.Ristretto.API.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
