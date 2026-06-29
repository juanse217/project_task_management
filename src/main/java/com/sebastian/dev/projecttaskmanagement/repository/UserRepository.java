package com.sebastian.dev.projecttaskmanagement.repository;

import com.sebastian.dev.projecttaskmanagement.repository.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
