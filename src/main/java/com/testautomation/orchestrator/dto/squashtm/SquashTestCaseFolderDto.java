package com.testautomation.orchestrator.dto.squashtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "SquashTM Test Case Folder information")
public class SquashTestCaseFolderDto {
    
    @JsonProperty("_type")
    @Schema(description = "Type of the entity", example = "test-case-folder")
    private String type;
    
    @JsonProperty("id")
    @Schema(description = "Folder ID", example = "123")
    private Long id;
    
    @JsonProperty("name")
    @Schema(description = "Folder name", example = "Test Suite 1")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "Folder description")
    private String description;
    
    @JsonProperty("parent")
    @Schema(description = "Parent folder information")
    private ParentDto parent;
    
    @JsonProperty("project")
    @Schema(description = "Project information")
    private ProjectRefDto project;
    
    @JsonProperty("_links")
    @Schema(description = "HATEOAS links")
    private SquashProjectDto.LinksDto links;
    
    // Constructors
    public SquashTestCaseFolderDto() {}
    
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ParentDto getParent() {
        return parent;
    }
    
    public void setParent(ParentDto parent) {
        this.parent = parent;
    }
    
    public ProjectRefDto getProject() {
        return project;
    }
    
    public void setProject(ProjectRefDto project) {
        this.project = project;
    }
    
    public SquashProjectDto.LinksDto getLinks() {
        return links;
    }
    
    public void setLinks(SquashProjectDto.LinksDto links) {
        this.links = links;
    }
    
    public static class ParentDto {
        @JsonProperty("_type")
        private String type;
        
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("name")
        private String name;
        
        public ParentDto() {}
        
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
    }
    
    public static class ProjectRefDto {
        @JsonProperty("_type")
        private String type;
        
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("name")
        private String name;
        
        public ProjectRefDto() {}
        
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
    }
}