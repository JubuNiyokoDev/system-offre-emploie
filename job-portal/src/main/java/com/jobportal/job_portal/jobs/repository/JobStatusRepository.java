package com.jobportal.job_portal.jobs.repository;

import com.jobportal.job_portal.jobs.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobStatusRepository extends JpaRepository<JobStatus, Long> {
    Optional<JobStatus> findByName(String name);
}