package com.jobportal.job_portal.jobs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "application_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}