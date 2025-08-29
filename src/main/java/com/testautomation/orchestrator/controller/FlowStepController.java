package com.testautomation.orchestrator.controller;

import com.testautomation.orchestrator.dto.FlowStepDto;
import com.testautomation.orchestrator.service.FlowStepService;
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
@RequestMapping("/api/flow-steps")
@Tag(name = "Flow Steps", description = "Flow Step Management API")
public class FlowStepController {

    private static final Logger logger = LoggerFactory.getLogger(FlowStepController.class);

    @Autowired
    private FlowStepService flowStepService;

    @PostMapping
    @Operation(summary = "Create a new flow step", description = "Create a new flow step configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flow step created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Referenced application not found")
    })
    public ResponseEntity<FlowStepDto> createFlowStep(
            @Valid @RequestBody FlowStepDto flowStepDto) {
        logger.info("Creating new flow step for application ID: {}", flowStepDto.getApplicationId());
        
        try {
            FlowStepDto createdFlowStep = flowStepService.createFlowStep(flowStepDto);
            return new ResponseEntity<>(createdFlowStep, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create flow step: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flow step by ID", description = "Retrieve a specific flow step by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flow step found"),
            @ApiResponse(responseCode = "404", description = "Flow step not found")
    })
    public ResponseEntity<FlowStepDto> getFlowStepById(
            @Parameter(description = "Flow step ID") @PathVariable Long id) {
        logger.debug("Fetching flow step with ID: {}", id);
        
        return flowStepService.getFlowStepById(id)
                .map(flowStep -> ResponseEntity.ok(flowStep))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all flow steps", description = "Retrieve all flow steps")
    @ApiResponse(responseCode = "200", description = "Flow steps retrieved successfully")
    public ResponseEntity<List<FlowStepDto>> getAllFlowSteps(
            @Parameter(description = "Filter by application ID") @RequestParam(required = false) Long applicationId) {
        logger.debug("Fetching flow steps with applicationId filter: {}", applicationId);
        
        List<FlowStepDto> flowSteps;
        if (applicationId != null) {
            flowSteps = flowStepService.getFlowStepsByApplicationId(applicationId);
        } else {
            flowSteps = flowStepService.getAllFlowSteps();
        }
        
        return ResponseEntity.ok(flowSteps);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update flow step", description = "Update an existing flow step")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flow step updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Flow step or referenced application not found")
    })
    public ResponseEntity<FlowStepDto> updateFlowStep(
            @Parameter(description = "Flow step ID") @PathVariable Long id,
            @Valid @RequestBody FlowStepDto flowStepDto) {
        logger.info("Updating flow step with ID: {}", id);
        
        try {
            FlowStepDto updatedFlowStep = flowStepService.updateFlowStep(id, flowStepDto);
            return ResponseEntity.ok(updatedFlowStep);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update flow step: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete flow step", description = "Delete a flow step by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flow step deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Flow step not found")
    })
    public ResponseEntity<Void> deleteFlowStep(
            @Parameter(description = "Flow step ID") @PathVariable Long id) {
        logger.info("Deleting flow step with ID: {}", id);
        
        try {
            flowStepService.deleteFlowStep(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete flow step: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}