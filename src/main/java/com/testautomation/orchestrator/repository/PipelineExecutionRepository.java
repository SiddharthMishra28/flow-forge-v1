package com.testautomation.orchestrator.repository;

import com.testautomation.orchestrator.enums.ExecutionStatus;
import com.testautomation.orchestrator.model.PipelineExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PipelineExecutionRepository extends JpaRepository<PipelineExecution, Long> {
    
    List<PipelineExecution> findByFlowExecutionId(UUID flowExecutionId);
    
    List<PipelineExecution> findByFlowId(Long flowId);
    
    List<PipelineExecution> findByFlowExecutionIdOrderByCreatedAt(UUID flowExecutionId);
    
    List<PipelineExecution> findByStatus(ExecutionStatus status);
    
    List<PipelineExecution> findByFlowStepId(Long flowStepId);
}