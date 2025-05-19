package com.jobportal.job_portal.jobs.entity;

import com.jobportal.job_portal.accounts.entity.CustomUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false, length = 100)
    private String company;

    @Column(nullable = false, length = 100)
    private String location;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private CustomUser publisher;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private JobStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(length = 100)
    private String salaryRange;

    @PrePersist
    @PreUpdate
    public void checkExpiration() {
        if (expiresAt.isBefore(LocalDateTime.now())) {
            if (status == null || !status.getName().equals("Expired")) {
                status = JobStatus.builder()
                        .name("Expired")
                        .description("Statut automatique pour les offres expirées")
                        .build();
            }
        }
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}