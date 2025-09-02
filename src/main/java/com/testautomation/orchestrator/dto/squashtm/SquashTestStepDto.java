package com.testautomation.orchestrator.dto.squashtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "SquashTM Test Step information")
public class SquashTestStepDto {
    
    @JsonProperty("_type")
    @Schema(description = "Type of the entity", example = "action-test-step")
    private String type;
    
    @JsonProperty("id")
    @Schema(description = "Test Step ID", example = "789")
    private Long id;
    
    @JsonProperty("index")
    @Schema(description = "Step index/order", example = "1")
    private Integer index;
    
    @JsonProperty("action")
    @Schema(description = "Test step action", example = "Click on login button")
    private String action;
    
    @JsonProperty("expected_result")
    @Schema(description = "Expected result", example = "User should be logged in")
    private String expectedResult;
    
    @JsonProperty("test_case")
    @Schema(description = "Parent test case information")
    private TestCaseRefDto testCase;
    
    @JsonProperty("_links")
    @Schema(description = "HATEOAS links")
    private SquashProjectDto.LinksDto links;
    
    // Constructors
    public SquashTestStepDto() {}
    
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
    
    public Integer getIndex() {
        return index;
    }
    
    public void setIndex(Integer index) {
        this.index = index;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getExpectedResult() {
        return expectedResult;
    }
    
    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }
    
    public TestCaseRefDto getTestCase() {
        return testCase;
    }
    
    public void setTestCase(TestCaseRefDto testCase) {
        this.testCase = testCase;
    }
    
    public SquashProjectDto.LinksDto getLinks() {
        return links;
    }
    
    public void setLinks(SquashProjectDto.LinksDto links) {
        this.links = links;
    }
    
    public static class TestCaseRefDto {
        @JsonProperty("_type")
        private String type;
        
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("name")
        private String name;
        
        public TestCaseRefDto() {}
        
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