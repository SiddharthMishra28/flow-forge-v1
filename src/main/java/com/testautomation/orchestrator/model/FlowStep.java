package com.testautomation.orchestrator.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "flow_steps")
public class FlowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @NotBlank
    @Column(name = "branch", nullable = false)
    private String branch;

    @NotBlank
    @Column(name = "test_tag", nullable = false)
    private String testTag;

    @NotBlank
    @Column(name = "test_stage", nullable = false)
    private String testStage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "squash_step_ids", columnDefinition = "json")
    private List<Long> squashStepIds;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "test_data_ids", columnDefinition = "json")
    private List<Long> testDataIds;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public FlowStep() {}

    public FlowStep(Long applicationId, String branch, String testTag, String testStage,
                   List<Long> squashStepIds, List<Long> testDataIds) {
        this.applicationId = applicationId;
        this.branch = branch;
        this.testTag = testTag;
        this.testStage = testStage;
        this.squashStepIds = squashStepIds;
        this.testDataIds = testDataIds;
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

    public List<Long> getTestDataIds() {
        return testDataIds;
    }

    public void setTestDataIds(List<Long> testDataIds) {
        this.testDataIds = testDataIds;
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