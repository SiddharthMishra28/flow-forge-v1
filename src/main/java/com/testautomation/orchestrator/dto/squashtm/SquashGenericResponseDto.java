package com.testautomation.orchestrator.dto.squashtm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Generic SquashTM API response that can handle any structure")
public class SquashGenericResponseDto {
    
    @JsonProperty("_embedded")
    @Schema(description = "Embedded data")
    private Map<String, Object> embedded;
    
    @JsonProperty("_links")
    @Schema(description = "HATEOAS links")
    private Map<String, Object> links;
    
    @JsonProperty("page")
    @Schema(description = "Pagination information")
    private Map<String, Object> page;
    
    // For single entity responses
    @JsonProperty("_type")
    private String type;
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    // Constructors
    public SquashGenericResponseDto() {}
    
    // Getters and Setters
    public Map<String, Object> getEmbedded() {
        return embedded;
    }
    
    public void setEmbedded(Map<String, Object> embedded) {
        this.embedded = embedded;
    }
    
    public Map<String, Object> getLinks() {
        return links;
    }
    
    public void setLinks(Map<String, Object> links) {
        this.links = links;
    }
    
    public Map<String, Object> getPage() {
        return page;
    }
    
    public void setPage(Map<String, Object> page) {
        this.page = page;
    }
    
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
    
    // Helper methods to extract data
    @SuppressWarnings("unchecked")
    public List<Object> getProjects() {
        if (embedded != null && embedded.containsKey("projects")) {
            return (List<Object>) embedded.get("projects");
        }
        return List.of();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getTestCaseFolders() {
        if (embedded != null && embedded.containsKey("test-case-folders")) {
            return (List<Object>) embedded.get("test-case-folders");
        }
        return List.of();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getTestCases() {
        if (embedded != null && embedded.containsKey("test-cases")) {
            return (List<Object>) embedded.get("test-cases");
        }
        return List.of();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> getTestSteps() {
        if (embedded != null && embedded.containsKey("test-steps")) {
            return (List<Object>) embedded.get("test-steps");
        }
        return List.of();
    }
}