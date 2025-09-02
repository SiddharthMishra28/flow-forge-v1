package com.testautomation.orchestrator.dto.squashtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;

@Schema(description = "SquashTM Test Case information")
public class SquashTestCaseDto {
    
    @JsonProperty("_type")
    @Schema(description = "Type of the entity", example = "test-case")
    private String type;
    
    @JsonProperty("id")
    @Schema(description = "Test Case ID", example = "456")
    private Long id;
    
    @JsonProperty("name")
    @Schema(description = "Test Case name", example = "Login Test")
    private String name;
    
    @JsonProperty("reference")
    @Schema(description = "Test Case reference", example = "TC-001")
    private String reference;
    
    @JsonProperty("description")
    @Schema(description = "Test Case description")
    private String description;
    
    @JsonProperty("script")
    @Schema(description = "Test Case script")
    private String script;
    
    @JsonProperty("charter")
    @Schema(description = "Test Case charter")
    private String charter;
    
    @JsonProperty("session_duration")
    @Schema(description = "Session duration for exploratory testing")
    private String sessionDuration;
    
    @JsonProperty("importance")
    @Schema(description = "Test Case importance", example = "HIGH")
    private String importance;
    
    @JsonProperty("status")
    @Schema(description = "Test Case status", example = "APPROVED")
    private String status;
    
    @JsonProperty("nature")
    @Schema(description = "Test Case nature", example = "FUNCTIONAL_TESTING")
    private String nature;
    
    @JsonProperty("type_test_case")
    @Schema(description = "Test Case type", example = "STANDARD_TEST_CASE")
    private String typeTestCase;
    
    @JsonProperty("created_by")
    @Schema(description = "Creator information")
    private String createdBy;
    
    @JsonProperty("created_on")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Schema(description = "Creation date")
    private OffsetDateTime createdOn;
    
    @JsonProperty("last_modified_by")
    @Schema(description = "Last modifier information")
    private String lastModifiedBy;
    
    @JsonProperty("last_modified_on")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Schema(description = "Last modification date")
    private OffsetDateTime lastModifiedOn;
    
    @JsonProperty("project")
    @Schema(description = "Project information")
    private SquashTestCaseFolderDto.ProjectRefDto project;
    
    @JsonProperty("test_steps")
    @Schema(description = "Test steps")
    private List<SquashTestStepDto> testSteps;
    
    @JsonProperty("_links")
    @Schema(description = "HATEOAS links")
    private SquashProjectDto.LinksDto links;
    
    // Constructors
    public SquashTestCaseDto() {}
    
    // Getters and Setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getReference() {
        return reference;
    }
    
    public void setReference(String reference) {
        this.reference = reference;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getScript() {
        return script;
    }
    
    public void setScript(String script) {
        this.script = script;
    }
    
    public String getCharter() {
        return charter;
    }
    
    public void setCharter(String charter) {
        this.charter = charter;
    }
    
    public String getSessionDuration() {
        return sessionDuration;
    }
    
    public void setSessionDuration(String sessionDuration) {
        this.sessionDuration = sessionDuration;
    }
    
    public String getImportance() {
        return importance;
    }
    
    public void setImportance(String importance) {
        this.importance = importance;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNature() {
        return nature;
    }
    
    public void setNature(String nature) {
        this.nature = nature;
    }
    
    public String getTypeTestCase() {
        return typeTestCase;
    }
    
    public void setTypeTestCase(String typeTestCase) {
        this.typeTestCase = typeTestCase;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }
    
    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }
    
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
    
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    
    public OffsetDateTime getLastModifiedOn() {
        return lastModifiedOn;
    }
    
    public void setLastModifiedOn(OffsetDateTime lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }
    
    public SquashTestCaseFolderDto.ProjectRefDto getProject() {
        return project;
    }
    
    public void setProject(SquashTestCaseFolderDto.ProjectRefDto project) {
        this.project = project;
    }
    
    public List<SquashTestStepDto> getTestSteps() {
        return testSteps;
    }
    
    public void setTestSteps(List<SquashTestStepDto> testSteps) {
        this.testSteps = testSteps;
    }
    
    public SquashProjectDto.LinksDto getLinks() {
        return links;
    }
    
    public void setLinks(SquashProjectDto.LinksDto links) {
        this.links = links;
    }
}