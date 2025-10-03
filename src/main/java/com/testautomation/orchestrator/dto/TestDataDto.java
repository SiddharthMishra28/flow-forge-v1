package com.testautomation.orchestrator.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

public class TestDataDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Auto-generated unique identifier")
    private Long dataId;

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Category is required")
    private String category;

    private String description;

    @NotNull(message = "Test data is required")
    private Map<String, String> testData;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Timestamp when the record was created")
    private LocalDateTime createdAt;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Timestamp when the record was last updated")
    private LocalDateTime updatedAt;

    // Constructors
    public TestDataDto() {}

    public TestDataDto(Long applicationId, String category, String description, Map<String, String> testData) {
        this.applicationId = applicationId;
        this.category = category;
        this.description = description;
        this.testData = testData;
    }

    // Getters and Setters
    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getTestData() {
        return testData;
    }

    public void setTestData(Map<String, String> testData) {
        this.testData = testData;
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