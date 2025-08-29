package com.testautomation.orchestrator.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FlowDto {

    private Long id;

    @NotNull(message = "Flow step IDs are required")
    @NotEmpty(message = "Flow must have at least one step")
    private List<Long> flowStepIds;

    @NotNull(message = "Squash test case ID is required")
    private Long squashTestCaseId;

    private Map<String, String> globalVariables;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public FlowDto() {}

    public FlowDto(List<Long> flowStepIds, Long squashTestCaseId, Map<String, String> globalVariables) {
        this.flowStepIds = flowStepIds;
        this.squashTestCaseId = squashTestCaseId;
        this.globalVariables = globalVariables;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getFlowStepIds() {
        return flowStepIds;
    }

    public void setFlowStepIds(List<Long> flowStepIds) {
        this.flowStepIds = flowStepIds;
    }

    public Long getSquashTestCaseId() {
        return squashTestCaseId;
    }

    public void setSquashTestCaseId(Long squashTestCaseId) {
        this.squashTestCaseId = squashTestCaseId;
    }

    public Map<String, String> getGlobalVariables() {
        return globalVariables;
    }

    public void setGlobalVariables(Map<String, String> globalVariables) {
        this.globalVariables = globalVariables;
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