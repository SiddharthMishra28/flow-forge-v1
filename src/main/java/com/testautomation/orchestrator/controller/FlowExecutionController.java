package com.testautomation.orchestrator.controller;

import com.testautomation.orchestrator.dto.FlowExecutionDto;
import com.testautomation.orchestrator.service.FlowExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
@Tag(name = "Flow Executions", description = "Flow Execution Management API")
public class FlowExecutionController {

    private static final Logger logger = LoggerFactory.getLogger(FlowExecutionController.class);

    @Autowired
    private FlowExecutionService flowExecutionService;

    @PostMapping("/flows/{flowId}/execute")
    @Operation(summary = "Execute a flow", description = "Trigger execution of a specific flow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Flow execution started successfully"),
            @ApiResponse(responseCode = "404", description = "Flow not found"),
            @ApiResponse(responseCode = "500", description = "Failed to start flow execution")
    })
    public ResponseEntity<FlowExecutionDto> executeFlow(
            @Parameter(description = "Flow ID to execute") @PathVariable Long flowId) {
        logger.info("Starting execution of flow ID: {}", flowId);
        
        try {
            // Create the flow execution synchronously to get the UUID
            FlowExecutionDto executionDto = flowExecutionService.createFlowExecution(flowId);
            
            // Start async execution
            flowExecutionService.executeFlowAsync(executionDto.getId());
            
            return new ResponseEntity<>(executionDto, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to start flow execution: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Unexpected error starting flow execution: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/flows/execute")
    @Operation(summary = "Execute multiple flows", description = "Trigger execution of multiple flows asynchronously with immediate response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Flow executions started successfully (all or partial) - returns immediately like single flow execution"),
            @ApiResponse(responseCode = "400", description = "Invalid flow IDs provided"),
            @ApiResponse(responseCode = "503", description = "Thread pool at capacity - some flows rejected")
    })
    public ResponseEntity<?> executeMultipleFlows(
            @Parameter(description = "Comma-separated flow IDs to execute", example = "1,2,3") 
            @RequestParam("trigger") String flowIds) {
        logger.info("Starting execution of multiple flows: {}", flowIds);
        
        try {
            Map<String, Object> result = flowExecutionService.executeMultipleFlows(flowIds);
            
            // Check if any flows were rejected due to capacity
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rejected = (List<Map<String, Object>>) result.get("rejected");
            
            if (rejected != null && !rejected.isEmpty()) {
                // Some flows were rejected due to thread pool capacity
                return new ResponseEntity<>(result, HttpStatus.SERVICE_UNAVAILABLE);
            } else {
                // All flows accepted for execution
                return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Failed to start multiple flow executions: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error starting multiple flow executions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/flows/executions")
    @Operation(summary = "Get multiple flow executions", description = "Get execution data for multiple flows. Supports pagination and sorting.")
    @ApiResponse(responseCode = "200", description = "Flow executions retrieved successfully")
    public ResponseEntity<?> getMultipleFlowExecutions(
            @Parameter(description = "Comma-separated flow IDs to get executions for", example = "1,2,3") 
            @RequestParam("triggered") String flowIds,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size,
            @Parameter(description = "Sort by field (e.g., 'startTime', 'endTime', 'status', 'createdAt')") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)") @RequestParam(required = false, defaultValue = "DESC") String sortDirection) {
        
        logger.debug("Fetching executions for multiple flows: {} with page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                    flowIds, page, size, sortBy, sortDirection);
        
        try {
            // If pagination parameters are provided, use pagination
            if (page != null || size != null) {
                int pageNumber = page != null ? page : 0;
                int pageSize = size != null ? size : 20; // default page size
                
                Sort sort;
                if (sortBy != null && !sortBy.trim().isEmpty()) {
                    // Use provided sort field and direction
                    Sort.Direction direction = sortDirection != null ? 
                        Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
                    sort = Sort.by(direction, sortBy);
                } else {
                    // Default sort: startTime DESC (most recent first)
                    sort = Sort.by(Sort.Direction.DESC, "startTime");
                }
                
                Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
                Page<FlowExecutionDto> executionsPage = flowExecutionService.getMultipleFlowExecutions(flowIds, pageable);
                
                return ResponseEntity.ok(executionsPage);
            } else {
                // Return all data without pagination (backward compatibility)
                // Apply default sorting (startTime DESC) even without pagination
                Sort sort;
                if (sortBy != null && !sortBy.trim().isEmpty()) {
                    // Use provided sort field and direction
                    Sort.Direction direction = sortDirection != null ? 
                        Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
                    sort = Sort.by(direction, sortBy);
                } else {
                    // Default sort: startTime DESC
                    sort = Sort.by(Sort.Direction.DESC, "startTime");
                }
                
                Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
                Page<FlowExecutionDto> executionsPage = flowExecutionService.getMultipleFlowExecutions(flowIds, pageable);
                
                return ResponseEntity.ok(executionsPage.getContent());
            }
        } catch (IllegalArgumentException e) {
            logger.error("Failed to get multiple flow executions: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error getting multiple flow executions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/flow-executions/{flowExecutionUUID}")
    @Operation(summary = "Get flow execution details", 
               description = "Get comprehensive flow execution details including nested flow, steps, applications, and pipeline executions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flow execution found"),
            @ApiResponse(responseCode = "404", description = "Flow execution not found")
    })
    public ResponseEntity<FlowExecutionDto> getFlowExecutionById(
            @Parameter(description = "Flow execution UUID") @PathVariable UUID flowExecutionUUID) {
        logger.debug("Fetching flow execution with UUID: {}", flowExecutionUUID);
        
        return flowExecutionService.getFlowExecutionById(flowExecutionUUID)
                .map(execution -> ResponseEntity.ok(execution))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/flows/{flowId}/executions")
    @Operation(summary = "Get flow executions by flow ID", 
               description = "Get all executions for a specific flow. Supports pagination and sorting.")
    @ApiResponse(responseCode = "200", description = "Flow executions retrieved successfully")
    public ResponseEntity<?> getFlowExecutionsByFlowId(
            @Parameter(description = "Flow ID") @PathVariable Long flowId,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size") @RequestParam(required = false) Integer size,
            @Parameter(description = "Sort by field (e.g., 'startTime', 'endTime', 'status', 'createdAt')") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)") @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        
        logger.debug("Fetching flow executions for flow ID: {} with page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                    flowId, page, size, sortBy, sortDirection);
        
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
            Page<FlowExecutionDto> executionsPage = flowExecutionService.getFlowExecutionsByFlowId(flowId, pageable);
            
            return ResponseEntity.ok(executionsPage);
        } else {
            // Return all data without pagination (backward compatibility)
            List<FlowExecutionDto> executions = flowExecutionService.getFlowExecutionsByFlowId(flowId);
            return ResponseEntity.ok(executions);
        }
    }

    @PostMapping("/flow-executions/{flowExecutionUUID}/replay/{failedFlowStepId}")
    @Operation(summary = "Replay/Resume a failed flow execution", 
               description = "Create a new flow execution that resumes from a specific failed step, automatically ingesting all test data and runtime variables up to the previous stage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Flow replay started successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request - flow execution must be failed or step not found"),
            @ApiResponse(responseCode = "404", description = "Flow execution or flow step not found"),
            @ApiResponse(responseCode = "500", description = "Failed to start flow replay")
    })
    public ResponseEntity<FlowExecutionDto> replayFlowExecution(
            @Parameter(description = "Original failed flow execution UUID") @PathVariable UUID flowExecutionUUID,
            @Parameter(description = "Flow step ID where the failure occurred") @PathVariable Long failedFlowStepId) {
        logger.info("Starting replay of flow execution ID: {} from failed step: {}", flowExecutionUUID, failedFlowStepId);
        
        try {
            // Create the replay flow execution synchronously to get the UUID
            FlowExecutionDto replayExecutionDto = flowExecutionService.createReplayFlowExecution(flowExecutionUUID, failedFlowStepId);
            
            // Start async replay execution
            flowExecutionService.executeReplayFlowAsync(replayExecutionDto.getId(), flowExecutionUUID, failedFlowStepId);
            
            return new ResponseEntity<>(replayExecutionDto, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to start flow replay: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error starting flow replay: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}