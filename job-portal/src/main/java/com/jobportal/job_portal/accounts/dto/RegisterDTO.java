package com.jobportal.job_portal.accounts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;

    @NotBlank(message = "La confirmation du mot de passe est requise")
    private String passwordConfirm;

    private Boolean isRecruiter = false;
}