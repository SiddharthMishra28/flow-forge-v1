package com.testautomation.orchestrator.dto.squashtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "SquashTM Projects response with pagination")
public class SquashProjectsResponseDto {
    
    @JsonProperty("_embedded")
    @Schema(description = "Embedded projects data")
    private EmbeddedProjectsDto embedded;
    
    @JsonProperty("_links")
    @Schema(description = "HATEOAS links")
    private SquashProjectDto.LinksDto links;
    
    @JsonProperty("page")
    @Schema(description = "Pagination information")
    private PageDto page;
    
    // Constructors
    public SquashProjectsResponseDto() {}
    
    public SquashProjectsResponseDto(EmbeddedProjectsDto embedded, SquashProjectDto.LinksDto links, PageDto page) {
        this.embedded = embedded;
        this.links = links;
        this.page = page;
    }
    
    // Getters and Setters
    public EmbeddedProjectsDto getEmbedded() {
        return embedded;
    }
    
    public void setEmbedded(EmbeddedProjectsDto embedded) {
        this.embedded = embedded;
    }
    
    public SquashProjectDto.LinksDto getLinks() {
        return links;
    }
    
    public void setLinks(SquashProjectDto.LinksDto links) {
        this.links = links;
    }
    
    public PageDto getPage() {
        return page;
    }
    
    public void setPage(PageDto page) {
        this.page = page;
    }
    
    public static class EmbeddedProjectsDto {
        @JsonProperty("projects")
        private List<SquashProjectDto> projects;
        
        @JsonProperty("project-templates")
        private List<SquashProjectDto> projectTemplates;
        
        public EmbeddedProjectsDto() {}
        
        public EmbeddedProjectsDto(List<SquashProjectDto> projects, List<SquashProjectDto> projectTemplates) {
            this.projects = projects;
            this.projectTemplates = projectTemplates;
        }
        
        public List<SquashProjectDto> getProjects() {
            return projects;
        }
        
        public void setProjects(List<SquashProjectDto> projects) {
            this.projects = projects;
        }
        
        public List<SquashProjectDto> getProjectTemplates() {
            return projectTemplates;
        }
        
        public void setProjectTemplates(List<SquashProjectDto> projectTemplates) {
            this.projectTemplates = projectTemplates;
        }
    }
    
    public static class PageDto {
        @JsonProperty("size")
        private Integer size;
        
        @JsonProperty("totalElements")
        private Long totalElements;
        
        @JsonProperty("totalPages")
        private Integer totalPages;
        
        @JsonProperty("number")
        private Integer number;
        
        public PageDto() {}
        
        public PageDto(Integer size, Long totalElements, Integer totalPages, Integer number) {
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.number = number;
        }
        
        public Integer getSize() {
            return size;
        }
        
        public void setSize(Integer size) {
            this.size = size;
        }
        
        public Long getTotalElements() {
            return totalElements;
        }
        
        public void setTotalElements(Long totalElements) {
            this.totalElements = totalElements;
        }
        
        public Integer getTotalPages() {
            return totalPages;
        }
        
        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }
        
        public Integer getNumber() {
            return number;
        }
        
        public void setNumber(Integer number) {
            this.number = number;
        }
    }
}