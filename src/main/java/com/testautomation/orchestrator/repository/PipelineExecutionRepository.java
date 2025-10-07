package com.testautomation.orchestrator.repository;

import com.testautomation.orchestrator.enums.ExecutionStatus;
import com.testautomation.orchestrator.model.PipelineExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PipelineExecutionRepository extends JpaRepository<PipelineExecution, Long> {
    
    List<PipelineExecution> findByFlowExecutionId(UUID flowExecutionId);
    
    List<PipelineExecution> findByFlowId(Long flowId);
    
    List<PipelineExecution> findByFlowExecutionIdOrderByCreatedAt(UUID flowExecutionId);
    
    List<PipelineExecution> findByStatus(ExecutionStatus status);
    
    List<PipelineExecution> findByFlowStepId(Long flowStepId);
    
    @Query("SELECT pe.pipelineId, COUNT(*), " +
           "SUM(CASE WHEN pe.status = 'PASSED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN pe.status = 'FAILED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN pe.status = 'CANCELLED' THEN 1 ELSE 0 END) " +
           "FROM PipelineExecution pe WHERE pe.pipelineId IS NOT NULL GROUP BY pe.pipelineId")
    List<Object[]> findExecutionStatsByPipeline();
    
    @Query("SELECT pe.pipelineId, " +
           "AVG(FUNCTION('TIMESTAMPDIFF', MINUTE, pe.startTime, pe.endTime)), " +
           "MIN(FUNCTION('TIMESTAMPDIFF', MINUTE, pe.startTime, pe.endTime)), " +
           "MAX(FUNCTION('TIMESTAMPDIFF', MINUTE, pe.startTime, pe.endTime)), " +
           "COUNT(*) " +
           "FROM PipelineExecution pe WHERE pe.endTime IS NOT NULL AND pe.pipelineId IS NOT NULL " +
           "GROUP BY pe.pipelineId")
    List<Object[]> findDurationStatsByPipeline();
    
    @Query("SELECT 'main', " +
           "SUM(CASE WHEN pe.status = 'FAILED' THEN 1 ELSE 0 END), " +
           "COUNT(*), MAX(pe.endTime) " +
           "FROM PipelineExecution pe")
    List<Object[]> findTopFailingBranches(@Param("limit") int limit);
    
    // Methods to support replay functionality
    List<PipelineExecution> findByOriginalFlowExecutionId(UUID originalFlowExecutionId);
    
    @Query("SELECT pe FROM PipelineExecution pe WHERE pe.flowExecutionId = :flowExecutionId OR pe.originalFlowExecutionId = :flowExecutionId ORDER BY pe.createdAt")
    List<PipelineExecution> findByFlowExecutionIdIncludingReplays(@Param("flowExecutionId") UUID flowExecutionId);
    
    @Query("SELECT pe FROM PipelineExecution pe WHERE pe.flowStepId = :flowStepId ORDER BY pe.createdAt")
    List<PipelineExecution> findByFlowStepIdIncludingReplays(@Param("flowStepId") Long flowStepId);
    
    // Method to find scheduled executions ready to resume
    List<PipelineExecution> findByStatusAndResumeTimeBefore(ExecutionStatus status, LocalDateTime resumeTime);
}