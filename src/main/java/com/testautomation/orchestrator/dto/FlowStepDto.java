package com.testautomation.orchestrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FlowStepDto {

    private Long id;

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Branch is required")
    @NotBlank(message = "Branch cannot be blank")
    private String branch;

    @NotNull(message = "Test tag is required")
    @NotBlank(message = "Test tag cannot be blank")
    private String testTag;

    @NotNull(message = "Test stage is required")
    @NotBlank(message = "Test stage cannot be blank")
    private String testStage;

    private List<Long> squashStepIds;
    private Map<String, String> initialTestData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public FlowStepDto() {}

    public FlowStepDto(Long applicationId, String branch, String testTag, String testStage,
                      List<Long> squashStepIds, Map<String, String> initialTestData) {
        this.applicationId = applicationId;
        this.branch = branch;
        this.testTag = testTag;
        this.testStage = testStage;
        this.squashStepIds = squashStepIds;
        this.initialTestData = initialTestData;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getTestTag() {
        return testTag;
    }

    public void setTestTag(String testTag) {
        this.testTag = testTag;
    }

    public String getTestStage() {
        return testStage;
    }

    public void setTestStage(String testStage) {
        this.testStage = testStage;
    }

    public List<Long> getSquashStepIds() {
        return squashStepIds;
    }

    public void setSquashStepIds(List<Long> squashStepIds) {
        this.squashStepIds = squashStepIds;
    }

    public Map<String, String> getInitialTestData() {
        return initialTestData;
    }

    public void setInitialTestData(Map<String, String> initialTestData) {
        this.initialTestData = initialTestData;
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