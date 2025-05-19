package com.jobportal.job_portal.jobs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobOfferDTO {
    private Long id;

    @NotBlank(message = "Le titre est requis")
    @Size(max = 200, message = "Le titre doit contenir au plus 200 caractères")
    private String title;

    @NotBlank(message = "La description est requise")
    private String description;

    @NotBlank(message = "L'entreprise est requise")
    @Size(max = 100, message = "L'entreprise doit contenir au plus 100 caractères")
    private String company;

    @NotBlank(message = "Le lieu est requis")
    @Size(max = 100, message = "Le lieu doit contenir au plus 100 caractères")
    private String location;

    @Size(max = 100, message = "La fourchette de salaire doit contenir au plus 100 caractères")
    private String salaryRange;

    private Long statusId;

    private LocalDateTime createdAt;

    @NotNull(message = "La date d'expiration est requise")
    private LocalDateTime expiresAt;

    private String publisherName;

    private boolean isExpired;
}