package com.testautomation.orchestrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ApplicationDto {

    private Long id;

    @NotNull(message = "GitLab project ID is required")
    @NotBlank(message = "GitLab project ID cannot be blank")
    private String gitlabProjectId;

    @NotNull(message = "Personal access token is required")
    @NotBlank(message = "Personal access token cannot be blank")
    private String personalAccessToken;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ApplicationDto() {}

    public ApplicationDto(String gitlabProjectId, String personalAccessToken) {
        this.gitlabProjectId = gitlabProjectId;
        this.personalAccessToken = personalAccessToken;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGitlabProjectId() {
        return gitlabProjectId;
    }

    public void setGitlabProjectId(String gitlabProjectId) {
        this.gitlabProjectId = gitlabProjectId;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
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