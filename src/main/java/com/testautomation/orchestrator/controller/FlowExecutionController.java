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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
               description = "Get all executions for a specific flow")
    @ApiResponse(responseCode = "200", description = "Flow executions retrieved successfully")
    public ResponseEntity<List<FlowExecutionDto>> getFlowExecutionsByFlowId(
            @Parameter(description = "Flow ID") @PathVariable Long flowId) {
        logger.debug("Fetching flow executions for flow ID: {}", flowId);
        
        List<FlowExecutionDto> executions = flowExecutionService.getFlowExecutionsByFlowId(flowId);
        return ResponseEntity.ok(executions);
    }
}