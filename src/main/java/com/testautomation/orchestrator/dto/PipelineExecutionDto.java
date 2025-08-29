package com.testautomation.orchestrator.dto;

import com.testautomation.orchestrator.enums.ExecutionStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class PipelineExecutionDto {

    private Long id;
    private Long flowId;
    private UUID flowExecutionId;
    private Long flowStepId;
    private Long pipelineId;
    private String pipelineUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Map<String, String> initialTestData;
    private Map<String, String> runtimeTestData;
    private ExecutionStatus status;
    private LocalDateTime createdAt;

    // Constructors
    public PipelineExecutionDto() {}

    public PipelineExecutionDto(Long flowId, UUID flowExecutionId, Long flowStepId, ExecutionStatus status) {
        this.flowId = flowId;
        this.flowExecutionId = flowExecutionId;
        this.flowStepId = flowStepId;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public UUID getFlowExecutionId() {
        return flowExecutionId;
    }

    public void setFlowExecutionId(UUID flowExecutionId) {
        this.flowExecutionId = flowExecutionId;
    }

    public Long getFlowStepId() {
        return flowStepId;
    }

    public void setFlowStepId(Long flowStepId) {
        this.flowStepId = flowStepId;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineUrl() {
        return pipelineUrl;
    }

    public void setPipelineUrl(String pipelineUrl) {
        this.pipelineUrl = pipelineUrl;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Map<String, String> getInitialTestData() {
        return initialTestData;
    }

    public void setInitialTestData(Map<String, String> initialTestData) {
        this.initialTestData = initialTestData;
    }

    public Map<String, String> getRuntimeTestData() {
        return runtimeTestData;
    }

    public void setRuntimeTestData(Map<String, String> runtimeTestData) {
        this.runtimeTestData = runtimeTestData;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}