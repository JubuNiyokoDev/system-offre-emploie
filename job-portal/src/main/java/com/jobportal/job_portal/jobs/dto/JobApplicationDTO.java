package com.jobportal.job_portal.jobs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobApplicationDTO {
    private Long id;

    @NotNull(message = "L'ID de l'offre est requis")
    private Long jobId;

    private String jobTitle;

    private String applicantName;

    private Long statusId;

    private LocalDateTime appliedAt;

    @NotBlank(message = "La lettre de motivation est requise")
    private String coverLetter;

    private String notes;
}