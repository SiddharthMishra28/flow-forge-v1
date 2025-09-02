package com.testautomation.orchestrator.dto.squashtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Generic SquashTM paged response")
public class SquashPagedResponseDto<T> {
    
    @JsonProperty("_embedded")
    @Schema(description = "Embedded data")
    private EmbeddedDataDto<T> embedded;
    
    @JsonProperty("_links")
    @Schema(description = "HATEOAS links")
    private SquashProjectDto.LinksDto links;
    
    @JsonProperty("page")
    @Schema(description = "Pagination information")
    private SquashProjectsResponseDto.PageDto page;
    
    // Constructors
    public SquashPagedResponseDto() {}
    
    public SquashPagedResponseDto(EmbeddedDataDto<T> embedded, SquashProjectDto.LinksDto links, SquashProjectsResponseDto.PageDto page) {
        this.embedded = embedded;
        this.links = links;
        this.page = page;
    }
    
    // Getters and Setters
    public EmbeddedDataDto<T> getEmbedded() {
        return embedded;
    }
    
    public void setEmbedded(EmbeddedDataDto<T> embedded) {
        this.embedded = embedded;
    }
    
    public SquashProjectDto.LinksDto getLinks() {
        return links;
    }
    
    public void setLinks(SquashProjectDto.LinksDto links) {
        this.links = links;
    }
    
    public SquashProjectsResponseDto.PageDto getPage() {
        return page;
    }
    
    public void setPage(SquashProjectsResponseDto.PageDto page) {
        this.page = page;
    }
    
    public static class EmbeddedDataDto<T> {
        @JsonProperty("test-case-folders")
        private List<T> testCaseFolders;
        
        @JsonProperty("test-cases")
        private List<T> testCases;
        
        @JsonProperty("test-steps")
        private List<T> testSteps;
        
        public EmbeddedDataDto() {}
        
        public EmbeddedDataDto(List<T> data) {
            // Try to determine the type and set appropriate field
            this.testCaseFolders = data;
            this.testCases = data;
            this.testSteps = data;
        }
        
        public List<T> getData() {
            // Return whichever list is not null/empty
            if (testCaseFolders != null && !testCaseFolders.isEmpty()) {
                return testCaseFolders;
            }
            if (testCases != null && !testCases.isEmpty()) {
                return testCases;
            }
            if (testSteps != null && !testSteps.isEmpty()) {
                return testSteps;
            }
            return List.of();
        }
        
        public void setData(List<T> data) {
            this.testCaseFolders = data;
        }
        
        public List<T> getTestCaseFolders() {
            return testCaseFolders;
        }
        
        public void setTestCaseFolders(List<T> testCaseFolders) {
            this.testCaseFolders = testCaseFolders;
        }
        
        public List<T> getTestCases() {
            return testCases;
        }
        
        public void setTestCases(List<T> testCases) {
            this.testCases = testCases;
        }
        
        public List<T> getTestSteps() {
            return testSteps;
        }
        
        public void setTestSteps(List<T> testSteps) {
            this.testSteps = testSteps;
        }
    }
}