package com.testautomation.orchestrator.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class CombinedFlowDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Auto-generated unique identifier")
    private Long id;

    @NotNull(message = "Flow steps are required")
    @NotEmpty(message = "Flow must have at least one step")
    @Valid
    @Schema(description = "List of flow steps with embedded test data")
    private List<CombinedFlowStepDto> flowSteps;

    @NotNull(message = "Squash test case ID is required")
    private Long squashTestCaseId;

    @NotNull(message = "Squash test case is required")
    private String squashTestCase;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Timestamp when the record was created")
    private LocalDateTime createdAt;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Timestamp when the record was last updated")
    private LocalDateTime updatedAt;

    // Constructors
    public CombinedFlowDto() {}

    public CombinedFlowDto(List<CombinedFlowStepDto> flowSteps, Long squashTestCaseId, String squashTestCase) {
        this.flowSteps = flowSteps;
        this.squashTestCaseId = squashTestCaseId;
        this.squashTestCase = squashTestCase;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CombinedFlowStepDto> getFlowSteps() {
        return flowSteps;
    }

    public void setFlowSteps(List<CombinedFlowStepDto> flowSteps) {
        this.flowSteps = flowSteps;
    }

    public Long getSquashTestCaseId() {
        return squashTestCaseId;
    }

    public void setSquashTestCaseId(Long squashTestCaseId) {
        this.squashTestCaseId = squashTestCaseId;
    }

    public String getSquashTestCase() {
        return squashTestCase;
    }

    public void setSquashTestCase(String squashTestCase) {
        this.squashTestCase = squashTestCase;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}