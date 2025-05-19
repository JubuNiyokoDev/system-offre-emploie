package com.jobportal.job_portal.jobs.service;

import com.jobportal.job_portal.accounts.entity.CustomUser;
import com.jobportal.job_portal.common.ApiException;
import com.jobportal.job_portal.jobs.dto.JobApplicationDTO;
import com.jobportal.job_portal.jobs.dto.JobOfferDTO;
import com.jobportal.job_portal.jobs.entity.ApplicationStatus;
import com.jobportal.job_portal.jobs.entity.JobApplication;
import com.jobportal.job_portal.jobs.entity.JobOffer;
import com.jobportal.job_portal.jobs.entity.JobStatus;
import com.jobportal.job_portal.jobs.repository.ApplicationStatusRepository;
import com.jobportal.job_portal.jobs.repository.JobApplicationRepository;
import com.jobportal.job_portal.jobs.repository.JobOfferRepository;
import com.jobportal.job_portal.jobs.repository.JobStatusRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobOfferRepository jobOfferRepository;
    private final JobStatusRepository jobStatusRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ApplicationStatusRepository applicationStatusRepository;

    public JobService(JobOfferRepository jobOfferRepository, JobStatusRepository jobStatusRepository,
            JobApplicationRepository jobApplicationRepository,
            ApplicationStatusRepository applicationStatusRepository) {
        this.jobOfferRepository = jobOfferRepository;
        this.jobStatusRepository = jobStatusRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.applicationStatusRepository = applicationStatusRepository;
    }

    public JobOfferDTO createJobOffer(JobOfferDTO jobOfferDTO, CustomUser user) {
        if (!user.getIsRecruiter()) {
            throw new ApiException("Seuls les recruteurs peuvent créer des offres", HttpStatus.FORBIDDEN);
        }
        if (jobOfferDTO.getExpiresAt() == null) {
            throw new ApiException("La date d'expiration est requise", HttpStatus.BAD_REQUEST);
        }
        if (jobOfferDTO.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("La date d'expiration doit être dans le futur", HttpStatus.BAD_REQUEST);
        }

        JobStatus status = jobOfferDTO.getStatusId() != null
                ? jobStatusRepository.findById(jobOfferDTO.getStatusId())
                        .orElseThrow(() -> new ApiException("Statut d'offre invalide", HttpStatus.BAD_REQUEST))
                : jobStatusRepository.findByName("active")
                        .orElseGet(() -> jobStatusRepository.save(
                                JobStatus.builder().name("active").description("Offre active").build()));

        JobOffer jobOffer = JobOffer.builder()
                .title(jobOfferDTO.getTitle())
                .description(jobOfferDTO.getDescription())
                .company(jobOfferDTO.getCompany())
                .location(jobOfferDTO.getLocation())
                .publisher(user)
                .status(status)
                .expiresAt(jobOfferDTO.getExpiresAt())
                .salaryRange(jobOfferDTO.getSalaryRange())
                .build();

        jobOffer = jobOfferRepository.save(jobOffer);
        return toJobOfferDTO(jobOffer);
    }

    public JobOfferDTO updateJobOffer(Long id, JobOfferDTO jobOfferDTO, CustomUser user) {
        JobOffer jobOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new ApiException("Offre non trouvée", HttpStatus.NOT_FOUND));

        if (!jobOffer.getPublisher().getId().equals(user.getId()) && !user.getIsAdmin()) {
            throw new ApiException("Vous n'êtes pas autorisé à modifier cette offre", HttpStatus.FORBIDDEN);
        }

        if (jobOfferDTO.getExpiresAt() != null && jobOfferDTO.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("La date d'expiration doit être dans le futur", HttpStatus.BAD_REQUEST);
        }

        if (jobOfferDTO.getTitle() != null)
            jobOffer.setTitle(jobOfferDTO.getTitle());
        if (jobOfferDTO.getDescription() != null)
            jobOffer.setDescription(jobOfferDTO.getDescription());
        if (jobOfferDTO.getCompany() != null)
            jobOffer.setCompany(jobOfferDTO.getCompany());
        if (jobOfferDTO.getLocation() != null)
            jobOffer.setLocation(jobOfferDTO.getLocation());
        if (jobOfferDTO.getExpiresAt() != null)
            jobOffer.setExpiresAt(jobOfferDTO.getExpiresAt());
        if (jobOfferDTO.getSalaryRange() != null)
            jobOffer.setSalaryRange(jobOfferDTO.getSalaryRange());
        if (jobOfferDTO.getStatusId() != null) {
            JobStatus status = jobStatusRepository.findById(jobOfferDTO.getStatusId())
                    .orElseThrow(() -> new ApiException("Statut d'offre invalide", HttpStatus.BAD_REQUEST));
            jobOffer.setStatus(status);
        }

        jobOffer = jobOfferRepository.save(jobOffer);
        return toJobOfferDTO(jobOffer);
    }

    public JobOfferDTO getJobOffer(Long id) {
        JobOffer jobOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new ApiException("Offre non trouvée", HttpStatus.NOT_FOUND));
        return toJobOfferDTO(jobOffer);
    }

    public List<JobOfferDTO> getAllJobOffers(String query, String location, String dateFilter) {
        List<JobOffer> jobOffers;
        if (query != null && !query.isEmpty()) {
            jobOffers = jobOfferRepository.searchByQuery(query);
        } else if (location != null && !location.isEmpty()) {
            jobOffers = jobOfferRepository.findByLocationIgnoreCase(location);
        } else if (dateFilter != null) {
            LocalDateTime date;
            switch (dateFilter.toLowerCase()) {
                case "today":
                    date = LocalDateTime.now().toLocalDate().atStartOfDay();
                    break;
                case "week":
                    date = LocalDateTime.now().minusDays(7);
                    break;
                case "month":
                    date = LocalDateTime.now().minusDays(30);
                    break;
                default:
                    throw new ApiException("Filtre de date invalide : utilisez 'today', 'week' ou 'month'",
                            HttpStatus.BAD_REQUEST);
            }
            jobOffers = jobOfferRepository.findByCreatedAtAfter(date);
        } else {
            jobOffers = jobOfferRepository.findAll();
        }
        return jobOffers.stream().map(this::toJobOfferDTO).collect(Collectors.toList());
    }

    public List<JobOfferDTO> getMyPublishedJobs(CustomUser user) {
        if (!user.getIsRecruiter()) {
            throw new ApiException("Seuls les recruteurs peuvent voir leurs offres publiées", HttpStatus.FORBIDDEN);
        }
        return jobOfferRepository.findByPublisherId(user.getId())
                .stream().map(this::toJobOfferDTO).collect(Collectors.toList());
    }

    public List<JobOfferDTO> getAvailableJobs(CustomUser user) {
        return jobOfferRepository.findByStatusNameAndExpiresAtAfterAndPublisherIdNot(
                "active", LocalDateTime.now(), user.getId())
                .stream().map(this::toJobOfferDTO).collect(Collectors.toList());
    }

    public void applyForJob(Long jobId, JobApplicationDTO jobApplicationDTO, CustomUser user) {
        JobOffer job = jobOfferRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Offre non trouvée", HttpStatus.NOT_FOUND));

        if (user.getIsRecruiter()) {
            throw new ApiException("Les recruteurs ne peuvent pas postuler à des offres", HttpStatus.FORBIDDEN);
        }
        if (job.getPublisher().getId().equals(user.getId())) {
            throw new ApiException("Vous ne pouvez pas postuler à votre propre offre", HttpStatus.FORBIDDEN);
        }
        if (job.isExpired()) {
            throw new ApiException("Cette offre a expiré", HttpStatus.BAD_REQUEST);
        }
        if (jobApplicationRepository.findByJobIdAndApplicantId(jobId, user.getId()).isPresent()) {
            throw new ApiException("Vous avez déjà postulé à cette offre", HttpStatus.BAD_REQUEST);
        }

        ApplicationStatus status = jobApplicationDTO.getStatusId() != null
                ? applicationStatusRepository.findById(jobApplicationDTO.getStatusId())
                        .orElseThrow(() -> new ApiException("Statut de candidature invalide", HttpStatus.BAD_REQUEST))
                : applicationStatusRepository.findByName("pending")
                        .orElseGet(() -> applicationStatusRepository.save(
                                ApplicationStatus.builder().name("pending").description("Candidature en attente")
                                        .build()));

        JobApplication application = JobApplication.builder()
                .job(job)
                .applicant(user)
                .status(status)
                .coverLetter(jobApplicationDTO.getCoverLetter())
                .notes(jobApplicationDTO.getNotes())
                .build();

        jobApplicationRepository.save(application);
    }

    public JobApplicationDTO updateApplicationStatus(Long id, Long statusId, CustomUser user) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Candidature non trouvée", HttpStatus.NOT_FOUND));

        if (!application.getJob().getPublisher().getId().equals(user.getId())) {
            throw new ApiException("Seul le recruteur peut modifier le statut de la candidature", HttpStatus.FORBIDDEN);
        }

        ApplicationStatus status = applicationStatusRepository.findById(statusId)
                .orElseThrow(() -> new ApiException("Statut de candidature invalide", HttpStatus.BAD_REQUEST));
        application.setStatus(status);
        application = jobApplicationRepository.save(application);
        return toJobApplicationDTO(application);
    }

    public JobApplicationDTO cancelApplication(Long id, CustomUser user) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Candidature non trouvée", HttpStatus.NOT_FOUND));

        if (!application.getApplicant().getId().equals(user.getId())) {
            throw new ApiException("Seul le candidat peut annuler sa candidature", HttpStatus.FORBIDDEN);
        }

        ApplicationStatus status = applicationStatusRepository.findByName("cancelled")
                .orElseGet(() -> applicationStatusRepository.save(
                        ApplicationStatus.builder().name("cancelled").description("Candidature annulée").build()));
        application.setStatus(status);
        application = jobApplicationRepository.save(application);
        return toJobApplicationDTO(application);
    }

    public List<JobApplicationDTO> getApplications(CustomUser user, String status) {
        List<JobApplication> applications;
        if (user.getIsRecruiter()) {
            applications = jobApplicationRepository.findByJobPublisherId(user.getId());
        } else {
            applications = jobApplicationRepository.findByApplicantId(user.getId());
        }
        if (status != null && !status.isEmpty()) {
            if (!applicationStatusRepository.findByName(status).isPresent()) {
                throw new ApiException("Statut de candidature invalide", HttpStatus.BAD_REQUEST);
            }
            applications = applications.stream()
                    .filter(app -> app.getStatus() != null && app.getStatus().getName().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        return applications.stream().map(this::toJobApplicationDTO).collect(Collectors.toList());
    }

    private JobOfferDTO toJobOfferDTO(JobOffer jobOffer) {
        JobOfferDTO dto = new JobOfferDTO();
        dto.setId(jobOffer.getId());
        dto.setTitle(jobOffer.getTitle());
        dto.setDescription(jobOffer.getDescription());
        dto.setCompany(jobOffer.getCompany());
        dto.setLocation(jobOffer.getLocation());
        dto.setSalaryRange(jobOffer.getSalaryRange());
        dto.setStatusId(jobOffer.getStatus() != null ? jobOffer.getStatus().getId() : null);
        dto.setCreatedAt(jobOffer.getCreatedAt());
        dto.setExpiresAt(jobOffer.getExpiresAt());
        dto.setPublisherName(jobOffer.getPublisher().getUsername());
        dto.setExpired(jobOffer.isExpired());
        return dto;
    }

    private JobApplicationDTO toJobApplicationDTO(JobApplication application) {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setId(application.getId());
        dto.setJobId(application.getJob().getId());
        dto.setJobTitle(application.getJob().getTitle());
        dto.setApplicantName(application.getApplicant().getUsername());
        dto.setStatusId(application.getStatus() != null ? application.getStatus().getId() : null);
        dto.setAppliedAt(application.getAppliedAt());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setNotes(application.getNotes());
        return dto;
    }
}