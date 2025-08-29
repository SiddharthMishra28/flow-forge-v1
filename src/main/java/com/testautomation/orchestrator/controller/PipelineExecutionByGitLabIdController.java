package com.testautomation.orchestrator.controller;

import com.testautomation.orchestrator.dto.PipelineExecutionDto;
import com.testautomation.orchestrator.service.PipelineExecutionService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/flow-executions")
@Tag(name = "Pipeline Executions by GitLab ID", description = "Pipeline Execution Lookup by GitLab Pipeline ID")
public class PipelineExecutionByGitLabIdController {

    private static final Logger logger = LoggerFactory.getLogger(PipelineExecutionByGitLabIdController.class);

    @Autowired
    private PipelineExecutionService pipelineExecutionService;

    @GetMapping("/{flowExecutionUUID}/gitlab-pipelines/{gitlabPipelineId}")
    @Operation(summary = "Get pipeline execution by GitLab pipeline ID", 
               description = "Get pipeline execution details using the GitLab pipeline ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pipeline execution found"),
            @ApiResponse(responseCode = "404", description = "Pipeline execution not found")
    })
    public ResponseEntity<PipelineExecutionDto> getPipelineExecutionByGitLabId(
            @Parameter(description = "Flow execution UUID") @PathVariable UUID flowExecutionUUID,
            @Parameter(description = "GitLab pipeline ID") @PathVariable Long gitlabPipelineId) {
        logger.debug("Fetching pipeline execution with GitLab pipeline ID: {} for flow execution: {}", gitlabPipelineId, flowExecutionUUID);
        
        List<PipelineExecutionDto> pipelineExecutions = pipelineExecutionService.getPipelineExecutionsByFlowExecutionId(flowExecutionUUID);
        
        return pipelineExecutions.stream()
                .filter(pipeline -> gitlabPipelineId.equals(pipeline.getPipelineId()))
                .findFirst()
                .map(pipeline -> ResponseEntity.ok(pipeline))
                .orElse(ResponseEntity.notFound().build());
    }
}