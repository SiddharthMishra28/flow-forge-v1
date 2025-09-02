package com.testautomation.orchestrator.controller;

import com.testautomation.orchestrator.dto.squashtm.*;
import com.testautomation.orchestrator.service.SquashTmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/squashtm")
@Tag(name = "SquashTM Integration", description = "SquashTM API Integration for Test Management")
public class SquashTmController {

    private static final Logger logger = LoggerFactory.getLogger(SquashTmController.class);

    @Autowired
    private SquashTmService squashTmService;

    @GetMapping("/test-connectivity")
    @Operation(summary = "Test SquashTM API connectivity", description = "Test connection to SquashTM API and return raw response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API connectivity test successful"),
            @ApiResponse(responseCode = "500", description = "API connectivity test failed")
    })
    public ResponseEntity<String> testApiConnectivity() {
        logger.info("Testing SquashTM API connectivity");
        
        try {
            String rawResponse = squashTmService.testApiConnectivity();
            return ResponseEntity.ok(rawResponse);
        } catch (Exception e) {
            logger.error("SquashTM API connectivity test failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("API connectivity test failed: " + e.getMessage());
        }
    }

    @GetMapping("/projects")
    @Operation(summary = "Get SquashTM projects", description = "Retrieve SquashTM projects with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Error communicating with SquashTM")
    })
    public ResponseEntity<SquashProjectsResponseDto> getProjects(
            @Parameter(description = "Page number (0-based)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") 
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Getting SquashTM projects - page: {}, size: {}", page, size);
        
        try {
            SquashProjectsResponseDto projects = squashTmService.getProjects(page, size);
            if (projects == null) {
                logger.warn("Received null response from SquashTM service");
                return ResponseEntity.internalServerError().build();
            }
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            logger.error("Error fetching SquashTM projects: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "Get SquashTM project by ID", description = "Retrieve a specific SquashTM project by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Error communicating with SquashTM")
    })
    public ResponseEntity<SquashProjectDto> getProjectById(
            @Parameter(description = "Project ID", example = "367") 
            @PathVariable Long projectId) {
        
        logger.info("Getting SquashTM project by ID: {}", projectId);
        
        try {
            SquashProjectDto project = squashTmService.getProjectById(projectId);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            logger.error("Error fetching SquashTM project {}: {}", projectId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test-case-folders")
    @Operation(summary = "Get SquashTM test case folders", description = "Retrieve SquashTM test case folders with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test case folders retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Error communicating with SquashTM")
    })
    public ResponseEntity<SquashPagedResponseDto<SquashTestCaseFolderDto>> getTestCaseFolders(
            @Parameter(description = "Page number (0-based)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") 
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Getting SquashTM test case folders - page: {}, size: {}", page, size);
        
        try {
            SquashPagedResponseDto<SquashTestCaseFolderDto> folders = squashTmService.getTestCaseFolders(page, size);
            return ResponseEntity.ok(folders);
        } catch (Exception e) {
            logger.error("Error fetching SquashTM test case folders: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test-case-folders/{folderId}")
    @Operation(summary = "Get SquashTM test case folder by ID", description = "Retrieve a specific SquashTM test case folder by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test case folder retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Test case folder not found"),
            @ApiResponse(responseCode = "500", description = "Error communicating with SquashTM")
    })
    public ResponseEntity<SquashTestCaseFolderDto> getTestCaseFolderById(
            @Parameter(description = "Folder ID", example = "123") 
            @PathVariable Long folderId) {
        
        logger.info("Getting SquashTM test case folder by ID: {}", folderId);
        
        try {
            SquashTestCaseFolderDto folder = squashTmService.getTestCaseFolderById(folderId);
            return ResponseEntity.ok(folder);
        } catch (Exception e) {
            logger.error("Error fetching SquashTM test case folder {}: {}", folderId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test-cases")
    @Operation(summary = "Get SquashTM test cases", description = "Retrieve SquashTM test cases with pagination and optional field filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test cases retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Error communicating with SquashTM")
    })
    public ResponseEntity<SquashPagedResponseDto<SquashTestCaseDto>> getTestCases(
            @Parameter(description = "Page number (0-based)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Comma-separated list of fields to include", example = "name,reference,script,charter,session_duration") 
            @RequestParam(required = false) String fields) {
        
        logger.info("Getting SquashTM test cases - page: {}, size: {}, fields: {}", page, size, fields);
        
        try {
            SquashPagedResponseDto<SquashTestCaseDto> testCases = squashTmService.getTestCases(page, size, fields);
            return ResponseEntity.ok(testCases);
        } catch (Exception e) {
            logger.error("Error fetching SquashTM test cases: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test-cases/{testCaseId}")
    @Operation(summary = "Get SquashTM test case by ID", description = "Retrieve a specific SquashTM test case by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test case retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Test case not found"),
            @ApiResponse(responseCode = "500", description = "Error communicating with SquashTM")
    })
    public ResponseEntity<SquashTestCaseDto> getTestCaseById(
            @Parameter(description = "Test Case ID", example = "456") 
            @PathVariable Long testCaseId) {
        
        logger.info("Getting SquashTM test case by ID: {}", testCaseId);
        
        try {
            SquashTestCaseDto testCase = squashTmService.getTestCaseById(testCaseId);
            return ResponseEntity.ok(testCase);
        } catch (Exception e) {
            logger.error("Error fetching SquashTM test case {}: {}", testCaseId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test-cases/{testCaseId}/steps")
    @Operation(summary = "Get SquashTM test steps", description = "Retrieve test steps for a specific SquashTM test case")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test steps retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Test case not found"),
            @ApiResponse(responseCode = "500", description = "Error communicating with SquashTM")
    })
    public ResponseEntity<List<SquashTestStepDto>> getTestSteps(
            @Parameter(description = "Test Case ID", example = "456") 
            @PathVariable Long testCaseId) {
        
        logger.info("Getting SquashTM test steps for test case: {}", testCaseId);
        
        try {
            List<SquashTestStepDto> testSteps = squashTmService.getTestSteps(testCaseId);
            return ResponseEntity.ok(testSteps);
        } catch (Exception e) {
            logger.error("Error fetching SquashTM test steps for test case {}: {}", testCaseId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}