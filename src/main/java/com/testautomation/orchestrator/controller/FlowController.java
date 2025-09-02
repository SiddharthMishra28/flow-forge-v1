package com.testautomation.orchestrator.controller;

import com.testautomation.orchestrator.dto.FlowDto;
import com.testautomation.orchestrator.dto.FlowTestCaseAssociationDto;
import com.testautomation.orchestrator.service.FlowService;
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
@RequestMapping("/api/flows")
@Tag(name = "Flows", description = "Flow Management API")
public class FlowController {

    private static final Logger logger = LoggerFactory.getLogger(FlowController.class);

    @Autowired
    private FlowService flowService;

    @PostMapping
    @Operation(summary = "Create a new flow", description = "Create a new test automation flow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flow created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or referenced flow steps not found")
    })
    public ResponseEntity<FlowDto> createFlow(
            @Valid @RequestBody FlowDto flowDto) {
        logger.info("Creating new flow with {} steps", flowDto.getFlowStepIds().size());
        
        try {
            FlowDto createdFlow = flowService.createFlow(flowDto);
            return new ResponseEntity<>(createdFlow, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create flow: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flow by ID", description = "Retrieve a specific flow by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flow found"),
            @ApiResponse(responseCode = "404", description = "Flow not found")
    })
    public ResponseEntity<FlowDto> getFlowById(
            @Parameter(description = "Flow ID") @PathVariable Long id) {
        logger.debug("Fetching flow with ID: {}", id);
        
        return flowService.getFlowById(id)
                .map(flow -> ResponseEntity.ok(flow))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all flows", description = "Retrieve all flows")
    @ApiResponse(responseCode = "200", description = "Flows retrieved successfully")
    public ResponseEntity<List<FlowDto>> getAllFlows(
            @Parameter(description = "Filter by Squash test case ID") @RequestParam(required = false) Long squashTestCaseId) {
        logger.debug("Fetching flows with squashTestCaseId filter: {}", squashTestCaseId);
        
        List<FlowDto> flows;
        if (squashTestCaseId != null) {
            flows = flowService.getFlowsBySquashTestCaseId(squashTestCaseId);
        } else {
            flows = flowService.getAllFlows();
        }
        
        return ResponseEntity.ok(flows);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update flow", description = "Update an existing flow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flow updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or referenced flow steps not found"),
            @ApiResponse(responseCode = "404", description = "Flow not found")
    })
    public ResponseEntity<FlowDto> updateFlow(
            @Parameter(description = "Flow ID") @PathVariable Long id,
            @Valid @RequestBody FlowDto flowDto) {
        logger.info("Updating flow with ID: {}", id);
        
        try {
            FlowDto updatedFlow = flowService.updateFlow(id, flowDto);
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
    @Operation(summary = "Delete flow", description = "Delete a flow by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flow deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Flow not found")
    })
    public ResponseEntity<Void> deleteFlow(
            @Parameter(description = "Flow ID") @PathVariable Long id) {
        logger.info("Deleting flow with ID: {}", id);
        
        try {
            flowService.deleteFlow(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete flow: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{flowId}/add-test-case")
    @Operation(summary = "Associate SquashTM test case with flow", description = "Add or update SquashTM test case association to an existing flow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test case associated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Flow not found")
    })
    public ResponseEntity<FlowDto> addTestCaseToFlow(
            @Parameter(description = "Flow ID") @PathVariable Long flowId,
            @Valid @RequestBody FlowTestCaseAssociationDto associationDto) {
        
        logger.info("Adding SquashTM test case {} to flow {}", associationDto.getSquashTestCaseId(), flowId);
        
        try {
            FlowDto updatedFlow = flowService.addTestCaseToFlow(flowId, associationDto.getSquashTestCaseId());
            return ResponseEntity.ok(updatedFlow);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to add test case to flow: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{flowId}/remove-test-case")
    @Operation(summary = "Remove SquashTM test case from flow", description = "Remove SquashTM test case association from an existing flow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test case association removed successfully"),
            @ApiResponse(responseCode = "404", description = "Flow not found")
    })
    public ResponseEntity<FlowDto> removeTestCaseFromFlow(
            @Parameter(description = "Flow ID") @PathVariable Long flowId) {
        
        logger.info("Removing SquashTM test case association from flow {}", flowId);
        
        try {
            FlowDto updatedFlow = flowService.removeTestCaseFromFlow(flowId);
            return ResponseEntity.ok(updatedFlow);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to remove test case from flow: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{flowId}/update-test-case")
    @Operation(summary = "Update SquashTM test case in flow", description = "Update SquashTM test case association in an existing flow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test case updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Flow not found")
    })
    public ResponseEntity<FlowDto> updateTestCaseInFlow(
            @Parameter(description = "Flow ID") @PathVariable Long flowId,
            @Valid @RequestBody FlowTestCaseAssociationDto associationDto) {
        
        logger.info("Updating SquashTM test case in flow {} to test case {}", flowId, associationDto.getSquashTestCaseId());
        
        try {
            FlowDto updatedFlow = flowService.updateTestCaseInFlow(flowId, associationDto.getSquashTestCaseId());
            return ResponseEntity.ok(updatedFlow);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update test case in flow: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}