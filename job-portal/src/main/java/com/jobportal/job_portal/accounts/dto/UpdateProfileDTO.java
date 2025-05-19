package com.jobportal.job_portal.accounts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileDTO {
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    @Email(message = "L'email doit être valide")
    private String email;

    @Size(max = 15, message = "Le numéro de téléphone doit contenir au plus 15 caractères")
    private String phoneNumber;
}