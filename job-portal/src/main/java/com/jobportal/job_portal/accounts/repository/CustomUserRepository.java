package com.jobportal.job_portal.accounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jobportal.job_portal.accounts.entity.CustomUser;

import java.util.Optional;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {
    Optional<CustomUser> findByUsername(String username);

    Optional<CustomUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}