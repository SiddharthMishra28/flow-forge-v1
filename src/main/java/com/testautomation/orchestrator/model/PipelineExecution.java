package com.testautomation.orchestrator.model;

import com.testautomation.orchestrator.enums.ExecutionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "pipeline_executions")
public class PipelineExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "flow_id", nullable = false)
    private Long flowId;

    @NotNull
    @Column(name = "flow_execution_id", nullable = false)
    private UUID flowExecutionId;

    @NotNull
    @Column(name = "flow_step_id", nullable = false)
    private Long flowStepId;

    @Column(name = "pipeline_id")
    private Long pipelineId;

    @Column(name = "pipeline_url")
    private String pipelineUrl;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "initial_test_data", columnDefinition = "json")
    private Map<String, String> initialTestData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "runtime_test_data", columnDefinition = "json")
    private Map<String, String> runtimeTestData;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExecutionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public PipelineExecution() {}

    public PipelineExecution(Long flowId, UUID flowExecutionId, Long flowStepId, 
                           Map<String, String> initialTestData, Map<String, String> runtimeTestData) {
        this.flowId = flowId;
        this.flowExecutionId = flowExecutionId;
        this.flowStepId = flowStepId;
        this.initialTestData = initialTestData;
        this.runtimeTestData = runtimeTestData;
        this.status = ExecutionStatus.RUNNING;
        this.startTime = LocalDateTime.now();
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