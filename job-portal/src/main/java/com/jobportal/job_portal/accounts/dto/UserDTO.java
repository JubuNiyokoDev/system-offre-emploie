package com.jobportal.job_portal.accounts.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Boolean isRecruiter;
    private Boolean isBanned;
}