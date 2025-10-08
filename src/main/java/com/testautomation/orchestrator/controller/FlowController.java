package com.testautomation.orchestrator.controller;

import com.testautomation.orchestrator.dto.CombinedFlowDto;
import com.testautomation.orchestrator.dto.FlowTestCaseAssociationDto;
import com.testautomation.orchestrator.service.CombinedFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flows")
@Tag(name = "Flows", description = "Flow Management API")
public class FlowController {

    private static final Logger logger = LoggerFactory.getLogger(FlowController.class);

    @Autowired
    private CombinedFlowService combinedFlowService;

    @PostMapping
    @Operation(summary = "Create a new flow", description = "Create a new test automation flow with embedded flow steps and test data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flow created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or referenced applications not found")
    })
    public ResponseEntity<CombinedFlowDto> createFlow(
            @Valid @RequestBody CombinedFlowDto combinedFlowDto) {
        logger.info("Creating new combined flow with {} steps", combinedFlowDto.getFlowSteps().size());
        
        try {
            CombinedFlowDto createdFlow = combinedFlowService.createCombinedFlow(combinedFlowDto);
            return new ResponseEntity<>(createdFlow, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create flow: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flow by ID", description = "Retrieve a specific flow by its ID with embedded flow steps and test data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flow found"),
            @ApiResponse(responseCode = "404", description = "Flow not found")
    })
    public ResponseEntity<CombinedFlowDto> getFlowById(
            @Parameter(description = "Flow ID") @PathVariable Long id) {
        logger.debug("Fetching combined flow with ID: {}", id);
        
        return combinedFlowService.getCombinedFlowById(id)
                .map(flow -> ResponseEntity.ok(flow))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all flows", description = "Retrieve all flows with embedded flow steps and test data. Supports pagination and sorting.")
    @ApiResponse(responseCode = "200", description = "Flows retrieved successfully")
    public ResponseEntity<?> getAllFlows(
            @Parameter(description = "Filter by Squash test case ID") @RequestParam(required = false) Long squashTestCaseId,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size,
            @Parameter(description = "Sort by field (e.g., 'id', 'createdAt', 'updatedAt')") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)") @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        
        logger.debug("Fetching combined flows with squashTestCaseId filter: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                    squashTestCaseId, page, size, sortBy, sortDirection);
        
        // If pagination parameters are provided, use pagination
        if (page != null || size != null) {
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? size : 20; // default page size
            
            Sort sort = Sort.unsorted();
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                Sort.Direction direction = Sort.Direction.fromString(sortDirection);
                sort = Sort.by(direction, sortBy);
            }
            
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            
            Page<CombinedFlowDto> flowsPage;
            if (squashTestCaseId != null) {
                flowsPage = combinedFlowService.getCombinedFlowsBySquashTestCaseId(squashTestCaseId, pageable);
            } else {
                flowsPage = combinedFlowService.getAllCombinedFlows(pageable);
            }
            
            return ResponseEntity.ok(flowsPage);
        } else {
            // Return all data without pagination (backward compatibility)
            List<CombinedFlowDto> flows;
            if (squashTestCaseId != null) {
                flows = combinedFlowService.getCombinedFlowsBySquashTestCaseId(squashTestCaseId);
            } else {
                flows = combinedFlowService.getAllCombinedFlows();
            }
            
            return ResponseEntity.ok(flows);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update flow", description = "Update an existing flow with embedded flow steps and test data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flow updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or referenced applications not found"),
            @ApiResponse(responseCode = "404", description = "Flow not found")
    })
    public ResponseEntity<CombinedFlowDto> updateFlow(
            @Parameter(description = "Flow ID") @PathVariable Long id,
            @Valid @RequestBody CombinedFlowDto combinedFlowDto) {
        logger.info("Updating combined flow with ID: {}", id);
        
        try {
            CombinedFlowDto updatedFlow = combinedFlowService.updateCombinedFlow(id, combinedFlowDto);
            return ResponseEntity.ok(updatedFlow);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update flow: {}", e.getMessage());
            if (e.getMessage().contains("not found with ID")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete flow", description = "Delete a flow by its ID along with all associated flow steps and test data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flow deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Flow not found")
    })
    public ResponseEntity<Void> deleteFlow(
            @Parameter(description = "Flow ID") @PathVariable Long id) {
        logger.info("Deleting combined flow with ID: {}", id);
        
        try {
            combinedFlowService.deleteCombinedFlow(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete flow: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}