package com.jobportal.job_portal.accounts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenDTO {
    @NotBlank(message = "Le token de rafraîchissement est requis")
    private String refreshToken;
}