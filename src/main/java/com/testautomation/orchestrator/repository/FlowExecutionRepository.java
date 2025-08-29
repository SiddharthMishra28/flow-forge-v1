package com.testautomation.orchestrator.repository;

import com.testautomation.orchestrator.enums.ExecutionStatus;
import com.testautomation.orchestrator.model.FlowExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlowExecutionRepository extends JpaRepository<FlowExecution, UUID> {
    
    List<FlowExecution> findByFlowId(Long flowId);
    
    List<FlowExecution> findByStatus(ExecutionStatus status);
    
    List<FlowExecution> findByFlowIdAndStatus(Long flowId, ExecutionStatus status);
}