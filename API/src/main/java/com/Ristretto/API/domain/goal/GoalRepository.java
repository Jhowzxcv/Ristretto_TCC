package com.Ristretto.API.domain.goal;

import com.Ristretto.API.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
