package com.jobportal.job_portal.accounts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "Le nom d'utilisateur est requis")
    private String username;

    @NotBlank(message = "Le mot de passe est requis")
    private String password;
}