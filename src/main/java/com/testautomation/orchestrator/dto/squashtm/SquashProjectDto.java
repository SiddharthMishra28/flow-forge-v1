package com.testautomation.orchestrator.dto.squashtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "SquashTM Project information")
public class SquashProjectDto {
    
    @JsonProperty("_type")
    @Schema(description = "Type of the entity", example = "project")
    private String type;
    
    @JsonProperty("id")
    @Schema(description = "Project ID", example = "367")
    private Long id;
    
    @JsonProperty("name")
    @Schema(description = "Project name", example = "sample project 1")
    private String name;
    
    @JsonProperty("_links")
    @Schema(description = "HATEOAS links")
    private LinksDto links;
    
    // Constructors
    public SquashProjectDto() {}
    
    public SquashProjectDto(String type, Long id, String name, LinksDto links) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.links = links;
    }
    
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
    
    public LinksDto getLinks() {
        return links;
    }
    
    public void setLinks(LinksDto links) {
        this.links = links;
    }
    
    public static class LinksDto {
        @JsonProperty("self")
        private SelfLinkDto self;
        
        public LinksDto() {}
        
        public LinksDto(SelfLinkDto self) {
            this.self = self;
        }
        
        public SelfLinkDto getSelf() {
            return self;
        }
        
        public void setSelf(SelfLinkDto self) {
            this.self = self;
        }
    }
    
    public static class SelfLinkDto {
        @JsonProperty("href")
        private String href;
        
        public SelfLinkDto() {}
        
        public SelfLinkDto(String href) {
            this.href = href;
        }
        
        public String getHref() {
            return href;
        }
        
        public void setHref(String href) {
            this.href = href;
        }
    }
}