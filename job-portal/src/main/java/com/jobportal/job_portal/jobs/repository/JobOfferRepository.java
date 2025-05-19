package com.jobportal.job_portal.jobs.repository;

import com.jobportal.job_portal.jobs.entity.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> findByPublisherId(Long publisherId);

    List<JobOffer> findByStatusNameAndExpiresAtAfterAndPublisherIdNot(String status, LocalDateTime date,
            Long publisherId);

    @Query("SELECT j FROM JobOffer j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(j.company) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<JobOffer> searchByQuery(String query);

    List<JobOffer> findByLocationIgnoreCase(String location);

    List<JobOffer> findByCreatedAtAfter(LocalDateTime date);
}