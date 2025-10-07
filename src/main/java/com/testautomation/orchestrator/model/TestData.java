package com.testautomation.orchestrator.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "test_data")
public class TestData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id")
    private Long dataId;

    @Column(name = "application_id")
    private Long applicationId;

    @NotNull
    @Column(name = "application_name", nullable = false)
    private String applicationName;

    @NotNull
    @Column(nullable = false)
    private String category;

    private String description;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables", columnDefinition = "json", nullable = false)
    private Map<String, String> variables;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public TestData() {}

    public TestData(Long applicationId, String applicationName, String category, String description, Map<String, String> variables) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.category = category;
        this.description = description;
        this.variables = variables;
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

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
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

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
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