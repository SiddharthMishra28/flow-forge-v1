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
@Table(name = "flow_executions")
public class FlowExecution {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "flow_id", nullable = false)
    private Long flowId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "runtime_variables", columnDefinition = "json")
    private Map<String, String> runtimeVariables;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExecutionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public FlowExecution() {
        this.id = UUID.randomUUID();
    }

    public FlowExecution(Long flowId, Map<String, String> runtimeVariables) {
        this();
        this.flowId = flowId;
        this.runtimeVariables = runtimeVariables;
        this.status = ExecutionStatus.RUNNING;
        this.startTime = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
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


    public Map<String, String> getRuntimeVariables() {
        return runtimeVariables;
    }

    public void setRuntimeVariables(Map<String, String> runtimeVariables) {
        this.runtimeVariables = runtimeVariables;
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