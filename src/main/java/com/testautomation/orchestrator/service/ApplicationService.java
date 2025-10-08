package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.ApplicationDto;
import com.testautomation.orchestrator.dto.ValidationResponseDto;
import com.testautomation.orchestrator.exception.GitLabValidationException;
import com.testautomation.orchestrator.model.Application;
import com.testautomation.orchestrator.repository.ApplicationRepository;
import com.testautomation.orchestrator.config.GitLabConfig;
import com.testautomation.orchestrator.util.GitLabApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private GitLabApiClient gitLabApiClient;

    @Autowired
    private GitLabConfig gitLabConfig;

    @Autowired
    private EncryptionService encryptionService;

    public ApplicationDto createApplication(ApplicationDto applicationDto) {
        logger.info("Creating new application for GitLab project: {}", applicationDto.getGitlabProjectId());
        
        if (applicationRepository.existsByGitlabProjectId(applicationDto.getGitlabProjectId())) {
            throw new IllegalArgumentException("Application with GitLab project ID " + 
                                             applicationDto.getGitlabProjectId() + " already exists");
        }
        
        // Validate GitLab connection before creating application
        ValidationResponseDto validationResponse = validateGitLabConnectionInternal(
            applicationDto.getPersonalAccessToken(), 
            applicationDto.getGitlabProjectId()
        );
        
        if (!validationResponse.isValid()) {
            throw new GitLabValidationException("GitLab connection validation failed: " + validationResponse.getMessage());
        }
        
        Application application = convertToEntity(applicationDto);
        // Set project name and URL from validation response
        application.setProjectName(validationResponse.getProjectName());
        application.setProjectUrl(validationResponse.getProjectUrl());
        
        Application savedApplication = applicationRepository.save(application);
        
        logger.info("Application created with ID: {}", savedApplication.getId());
        return convertToDto(savedApplication);
    }

    @Transactional(readOnly = true)
    public Optional<ApplicationDto> getApplicationById(Long id) {
        logger.debug("Fetching application with ID: {}", id);
        return applicationRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<ApplicationDto> getAllApplications() {
        logger.debug("Fetching all applications");
        return applicationRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ApplicationDto> getAllApplications(Pageable pageable) {
        logger.debug("Fetching all applications with pagination: {}", pageable);
        return applicationRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public ApplicationDto updateApplication(Long id, ApplicationDto applicationDto) {
        logger.info("Updating application with ID: {}", id);
        
        Application existingApplication = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + id));
        
        // Check if GitLab project ID is being changed and if it conflicts with existing ones
        if (!existingApplication.getGitlabProjectId().equals(applicationDto.getGitlabProjectId()) &&
            applicationRepository.existsByGitlabProjectId(applicationDto.getGitlabProjectId())) {
            throw new IllegalArgumentException("Application with GitLab project ID " + 
                                             applicationDto.getGitlabProjectId() + " already exists");
        }
        
        // Validate GitLab connection if project ID or token has changed
        boolean projectIdChanged = !existingApplication.getGitlabProjectId().equals(applicationDto.getGitlabProjectId());
        // Decrypt existing token to compare with new token
        String existingDecryptedToken = encryptionService.decrypt(existingApplication.getPersonalAccessToken());
        boolean tokenChanged = !existingDecryptedToken.equals(applicationDto.getPersonalAccessToken());
        
        if (projectIdChanged || tokenChanged) {
            ValidationResponseDto validationResponse = validateGitLabConnectionInternal(
                applicationDto.getPersonalAccessToken(), 
                applicationDto.getGitlabProjectId()
            );
            
            if (!validationResponse.isValid()) {
                throw new GitLabValidationException("GitLab connection validation failed: " + validationResponse.getMessage());
            }
            
            // Update project name and URL from validation response
            existingApplication.setProjectName(validationResponse.getProjectName());
            existingApplication.setProjectUrl(validationResponse.getProjectUrl());
        }
        
        existingApplication.setGitlabProjectId(applicationDto.getGitlabProjectId());
        // Encrypt the new personal access token before saving
        existingApplication.setPersonalAccessToken(encryptionService.encrypt(applicationDto.getPersonalAccessToken()));
        existingApplication.setApplicationName(applicationDto.getApplicationName());
        existingApplication.setApplicationDescription(applicationDto.getApplicationDescription());
        
        Application updatedApplication = applicationRepository.save(existingApplication);
        
        logger.info("Application updated successfully with ID: {}", updatedApplication.getId());
        return convertToDto(updatedApplication);
    }

    public void deleteApplication(Long id) {
        logger.info("Deleting application with ID: {}", id);
        
        if (!applicationRepository.existsById(id)) {
            throw new IllegalArgumentException("Application not found with ID: " + id);
        }
        
        applicationRepository.deleteById(id);
        logger.info("Application deleted successfully with ID: {}", id);
    }

    /**
     * Get decrypted personal access token for an application.
     * This method is intended for use by other services that need the actual token.
     */
    public String getDecryptedPersonalAccessToken(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + applicationId));
        return encryptionService.decrypt(application.getPersonalAccessToken());
    }

    /**
     * Get decrypted personal access token by GitLab project ID.
     * This method is intended for use by other services that need the actual token.
     */
    public String getDecryptedPersonalAccessTokenByProjectId(String gitlabProjectId) {
        Application application = applicationRepository.findByGitlabProjectId(gitlabProjectId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with GitLab project ID: " + gitlabProjectId));
        return encryptionService.decrypt(application.getPersonalAccessToken());
    }

    public ValidationResponseDto validateGitLabConnection(String accessToken, String projectId) {
        return validateGitLabConnectionInternal(accessToken, projectId);
    }

    private ValidationResponseDto validateGitLabConnectionInternal(String accessToken, String projectId) {
        logger.info("Validating GitLab connection for project: {}", projectId);
        
        try {
            GitLabApiClient.GitLabProjectResponse projectResponse = gitLabApiClient
                .validateConnection(gitLabConfig.getBaseUrl(), projectId, accessToken)
                .block(); // Blocking call for synchronous response
            
            if (projectResponse != null) {
                logger.info("GitLab connection validation successful for project: {} ({})", 
                           projectResponse.getName(), projectResponse.getNameWithNamespace());
                
                return new ValidationResponseDto(
                    true,
                    "GitLab connection successful",
                    projectResponse.getNameWithNamespace(),
                    projectResponse.getWebUrl()
                );
            } else {
                logger.warn("GitLab connection validation failed - no response received for project: {}", projectId);
                return new ValidationResponseDto(false, "GitLab connection failed - no response received");
            }
        } catch (Exception e) {
            logger.error("GitLab connection validation failed for project {}: {}", projectId, e.getMessage());
            
            String errorMessage = "GitLab connection failed";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("401")) {
                    errorMessage = "Invalid access token or insufficient permissions";
                } else if (e.getMessage().contains("403")) {
                    errorMessage = "Access forbidden - check token permissions";
                } else if (e.getMessage().contains("404")) {
                    errorMessage = "Project not found or access denied";
                } else if (e.getMessage().contains("timeout") || e.getMessage().contains("TimeoutException")) {
                    errorMessage = "Connection timeout - GitLab server may be unreachable";
                } else {
                    errorMessage = "GitLab connection failed: " + e.getMessage();
                }
            }
            
            // For validation endpoint, return ValidationResponseDto with error
            return new ValidationResponseDto(false, errorMessage);
        }
    }

    private Application convertToEntity(ApplicationDto dto) {
        Application application = new Application();
        application.setGitlabProjectId(dto.getGitlabProjectId());
        // Encrypt the personal access token before saving
        application.setPersonalAccessToken(encryptionService.encrypt(dto.getPersonalAccessToken()));
        application.setApplicationName(dto.getApplicationName());
        application.setApplicationDescription(dto.getApplicationDescription());
        application.setProjectName(dto.getProjectName());
        application.setProjectUrl(dto.getProjectUrl());
        // Set default token status as ACTIVE for new applications
        if (dto.getTokenStatus() != null) {
            application.setTokenStatus(dto.getTokenStatus());
        }
        return application;
    }

    private ApplicationDto convertToDto(Application entity) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(entity.getId());
        dto.setGitlabProjectId(entity.getGitlabProjectId());
        // Don't set the personal access token in response (it's write-only)
        // dto.setPersonalAccessToken() is intentionally omitted
        dto.setApplicationName(entity.getApplicationName());
        dto.setApplicationDescription(entity.getApplicationDescription());
        dto.setProjectName(entity.getProjectName());
        dto.setProjectUrl(entity.getProjectUrl());
        dto.setTokenStatus(entity.getTokenStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}