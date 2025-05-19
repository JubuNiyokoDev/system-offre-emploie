package com.jobportal.job_portal.jobs.controller;

import com.jobportal.job_portal.accounts.entity.CustomUser;
import com.jobportal.job_portal.common.ApiException;
import com.jobportal.job_portal.jobs.dto.JobApplicationDTO;
import com.jobportal.job_portal.jobs.dto.JobOfferDTO;
import com.jobportal.job_portal.jobs.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> createJobOffer(@Valid @RequestBody JobOfferDTO jobOfferDTO,
            @AuthenticationPrincipal CustomUser user) {
        if (user == null) {
            throw new ApiException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
        }
        JobOfferDTO createdJob = jobService.createJobOffer(jobOfferDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("success", "Offre créée avec succès", createdJob));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobOffer(@PathVariable Long id) {
        JobOfferDTO jobOffer = jobService.getJobOffer(id);
        return ResponseEntity.ok(new ApiResponse("success", "Offre récupérée", jobOffer));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateJobOffer(@PathVariable Long id, @Valid @RequestBody JobOfferDTO jobOfferDTO,
            @AuthenticationPrincipal CustomUser user) {
        if (user == null) {
            throw new ApiException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
        }
        JobOfferDTO updatedJob = jobService.updateJobOffer(id, jobOfferDTO, user);
        return ResponseEntity.ok(new ApiResponse("success", "Offre mise à jour", updatedJob));
    }

    @GetMapping
    public ResponseEntity<?> getAllJobOffers(@RequestParam(required = false) String q,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String date_filter) {
        List<JobOfferDTO> jobOffers = jobService.getAllJobOffers(q, location, date_filter);
        return ResponseEntity.ok(new ApiResponse("success", "Offres récupérées", jobOffers));
    }

    @GetMapping("/my-published-jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getMyPublishedJobs(@AuthenticationPrincipal CustomUser user) {
        if (user == null) {
            throw new ApiException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
        }
        List<JobOfferDTO> jobs = jobService.getMyPublishedJobs(user);
        return ResponseEntity.ok(new ApiResponse("success", "Offres publiées récupérées", jobs));
    }

    @GetMapping("/available-jobs")
    public ResponseEntity<?> getAvailableJobs(@AuthenticationPrincipal CustomUser user) {
        if (user == null) {
            throw new ApiException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
        }
        List<JobOfferDTO> jobs = jobService.getAvailableJobs(user);
        return ResponseEntity.ok(new ApiResponse("success", "Offres disponibles récupérées", jobs));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<?> applyForJob(@PathVariable Long id, @Valid @RequestBody JobApplicationDTO jobApplicationDTO,
            @AuthenticationPrincipal CustomUser user) {
        if (user == null) {
            throw new ApiException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
        }
        jobService.applyForJob(id, jobApplicationDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("success", "Candidature envoyée avec succès", null));
    }

    @PostMapping("/applications")
    public ResponseEntity<?> createApplication(@Valid @RequestBody JobApplicationDTO jobApplicationDTO,
            @AuthenticationPrincipal CustomUser user) {
        if (user == null) {
            throw new ApiException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
        }
        jobService.applyForJob(jobApplicationDTO.getJobId(), jobApplicationDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("success", "Candidature créée avec succès", null));
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getApplications(@AuthenticationPrincipal CustomUser user,
            @RequestParam(required = false) String status) {
        if (user == null) {
            throw new ApiException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
        }
        List<JobApplicationDTO> applications = jobService.getApplications(user, status);
        return ResponseEntity.ok(new ApiResponse("success", "Candidatures récupérées", applications));
    }

    @PostMapping("/applications/{id}/update-status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long id, @RequestBody Long statusId,
            @AuthenticationPrincipal CustomUser user) {
        if (user == null) {
            throw new ApiException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
        }
        JobApplicationDTO updatedApplication = jobService.updateApplicationStatus(id, statusId, user);
        return ResponseEntity.ok(new ApiResponse("success", "Statut mis à jour avec succès", updatedApplication));
    }

    @PostMapping("/applications/{id}/cancel")
    public ResponseEntity<?> cancelApplication(@PathVariable Long id, @AuthenticationPrincipal CustomUser user) {
        if (user == null) {
            throw new ApiException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
        }
        JobApplicationDTO cancelledApplication = jobService.cancelApplication(id, user);
        return ResponseEntity.ok(new ApiResponse("success", "Candidature annulée avec succès", cancelledApplication));
    }

    record ApiResponse(String status, String message, Object data) {
    }
}