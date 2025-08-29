package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.ApplicationDto;
import com.testautomation.orchestrator.model.Application;
import com.testautomation.orchestrator.repository.ApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ApplicationDto createApplication(ApplicationDto applicationDto) {
        logger.info("Creating new application for GitLab project: {}", applicationDto.getGitlabProjectId());
        
        if (applicationRepository.existsByGitlabProjectId(applicationDto.getGitlabProjectId())) {
            throw new IllegalArgumentException("Application with GitLab project ID " + 
                                             applicationDto.getGitlabProjectId() + " already exists");
        }
        
        Application application = convertToEntity(applicationDto);
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
        
        existingApplication.setGitlabProjectId(applicationDto.getGitlabProjectId());
        existingApplication.setPersonalAccessToken(applicationDto.getPersonalAccessToken());
        
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

    private Application convertToEntity(ApplicationDto dto) {
        Application application = new Application();
        application.setGitlabProjectId(dto.getGitlabProjectId());
        application.setPersonalAccessToken(dto.getPersonalAccessToken());
        return application;
    }

    private ApplicationDto convertToDto(Application entity) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(entity.getId());
        dto.setGitlabProjectId(entity.getGitlabProjectId());
        dto.setPersonalAccessToken(entity.getPersonalAccessToken());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}