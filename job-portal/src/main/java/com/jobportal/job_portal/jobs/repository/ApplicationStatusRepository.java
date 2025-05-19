package com.jobportal.job_portal.jobs.repository;

import com.jobportal.job_portal.jobs.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, Long> {
    Optional<ApplicationStatus> findByName(String name);
}