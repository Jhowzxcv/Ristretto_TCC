package com.Ristretto.API.domain.subTask;

import com.Ristretto.API.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubTaskRepository extends JpaRepository<SubTask ,Long> {
}
