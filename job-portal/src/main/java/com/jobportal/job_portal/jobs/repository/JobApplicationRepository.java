package com.jobportal.job_portal.jobs.repository;

import com.jobportal.job_portal.jobs.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Optional<JobApplication> findByJobIdAndApplicantId(Long jobId, Long applicantId);

    List<JobApplication> findByApplicantId(Long applicantId);

    List<JobApplication> findByJobPublisherId(Long publisherId);

    List<JobApplication> findByStatusName(String status);
}