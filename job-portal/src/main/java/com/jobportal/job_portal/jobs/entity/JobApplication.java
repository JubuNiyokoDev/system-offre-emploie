package com.jobportal.job_portal.jobs.entity;

import com.jobportal.job_portal.accounts.entity.CustomUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications", uniqueConstraints = @UniqueConstraint(columnNames = { "job_id", "applicant_id" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobOffer job;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private CustomUser applicant;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private ApplicationStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime appliedAt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String coverLetter;

    @Column(columnDefinition = "TEXT")
    private String notes;
}