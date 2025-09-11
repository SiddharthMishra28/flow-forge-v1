package com.testautomation.orchestrator.controller;

import com.testautomation.orchestrator.dto.ApplicationDto;
import com.testautomation.orchestrator.dto.ValidationRequestDto;
import com.testautomation.orchestrator.dto.ValidationResponseDto;
import com.testautomation.orchestrator.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "GitLab Application Management API")
public class ApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Create a new application", description = "Create a new GitLab application configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Application with GitLab project ID already exists")
    })
    public ResponseEntity<ApplicationDto> createApplication(
            @Valid @RequestBody ApplicationDto applicationDto) {
        logger.info("Creating new application for GitLab project: {}", applicationDto.getGitlabProjectId());
        
        try {
            ApplicationDto createdApplication = applicationService.createApplication(applicationDto);
            return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create application: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID", description = "Retrieve a specific application by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application found"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<ApplicationDto> getApplicationById(
            @Parameter(description = "Application ID") @PathVariable Long id) {
        logger.debug("Fetching application with ID: {}", id);
        
        return applicationService.getApplicationById(id)
                .map(application -> ResponseEntity.ok(application))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all applications", description = "Retrieve all GitLab applications")
    @ApiResponse(responseCode = "200", description = "Applications retrieved successfully")
    public ResponseEntity<List<ApplicationDto>> getAllApplications() {
        logger.debug("Fetching all applications");
        
        List<ApplicationDto> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update application", description = "Update an existing application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "GitLab project ID already exists")
    })
    public ResponseEntity<ApplicationDto> updateApplication(
            @Parameter(description = "Application ID") @PathVariable Long id,
            @Valid @RequestBody ApplicationDto applicationDto) {
        logger.info("Updating application with ID: {}", id);
        
        try {
            ApplicationDto updatedApplication = applicationService.updateApplication(id, applicationDto);
            return ResponseEntity.ok(updatedApplication);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update application: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete application", description = "Delete an application by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Application deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<Void> deleteApplication(
            @Parameter(description = "Application ID") @PathVariable Long id) {
        logger.info("Deleting application with ID: {}", id);
        
        try {
            applicationService.deleteApplication(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete application: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate GitLab connection", description = "Validate GitLab connectivity using access token and project ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "GitLab connection validated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Invalid access token"),
            @ApiResponse(responseCode = "403", description = "Access forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "GitLab connection failed")
    })
    public ResponseEntity<ValidationResponseDto> validateGitLabConnection(
            @Valid @RequestBody ValidationRequestDto validationRequest) {
        logger.info("Validating GitLab connection for project: {}", validationRequest.getProjectId());
        
        try {
            ValidationResponseDto response = applicationService.validateGitLabConnection(
                validationRequest.getAccessToken(), 
                validationRequest.getProjectId()
            );
            
            if (response.isValid()) {
                logger.info("GitLab connection validation successful for project: {}", validationRequest.getProjectId());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("GitLab connection validation failed for project: {}", validationRequest.getProjectId());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("GitLab connection validation error for project {}: {}", 
                        validationRequest.getProjectId(), e.getMessage());
            
            ValidationResponseDto errorResponse = new ValidationResponseDto(false, 
                "GitLab connection failed: " + e.getMessage());
            
            // Determine appropriate HTTP status based on error type
            if (e.getMessage().contains("401") || e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            } else if (e.getMessage().contains("403") || e.getMessage().contains("Forbidden")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            } else if (e.getMessage().contains("404") || e.getMessage().contains("Not Found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
    }
}