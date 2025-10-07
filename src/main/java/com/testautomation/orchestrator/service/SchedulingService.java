package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.enums.ExecutionStatus;
import com.testautomation.orchestrator.model.InvokeTimer;
import com.testautomation.orchestrator.model.PipelineExecution;
import com.testautomation.orchestrator.repository.PipelineExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);

    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;

    @Autowired
    private FlowExecutionService flowExecutionService;

    /**
     * Calculates the resume time based on InvokeTimer configuration
     */
    public LocalDateTime calculateResumeTime(LocalDateTime previousStepEndTime, InvokeTimer invokeTimer) {
        if (invokeTimer == null) {
            return null;
        }

        LocalDateTime resumeTime = previousStepEndTime;

        // Add days
        if (invokeTimer.getDays() != null && !invokeTimer.getDays().isEmpty()) {
            try {
                String daysStr = invokeTimer.getDays().replace("+", "");
                int days = Integer.parseInt(daysStr);
                resumeTime = resumeTime.plusDays(days);
            } catch (NumberFormatException e) {
                logger.warn("Invalid days format in InvokeTimer: {}", invokeTimer.getDays());
            }
        }

        // Add hours
        if (invokeTimer.getHours() != null && !invokeTimer.getHours().isEmpty()) {
            try {
                String hoursStr = invokeTimer.getHours().replace("+", "");
                int hours = Integer.parseInt(hoursStr);
                resumeTime = resumeTime.plusHours(hours);
            } catch (NumberFormatException e) {
                logger.warn("Invalid hours format in InvokeTimer: {}", invokeTimer.getHours());
            }
        }

        // Add minutes
        if (invokeTimer.getMinutes() != null && !invokeTimer.getMinutes().isEmpty()) {
            try {
                String minutesStr = invokeTimer.getMinutes().replace("+", "");
                int minutes = Integer.parseInt(minutesStr);
                resumeTime = resumeTime.plusMinutes(minutes);
            } catch (NumberFormatException e) {
                logger.warn("Invalid minutes format in InvokeTimer: {}", invokeTimer.getMinutes());
            }
        }

        return resumeTime;
    }

    /**
     * Background scheduler that checks for scheduled pipeline executions that are ready to resume
     * Runs every minute
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processScheduledExecutions() {
        logger.debug("Checking for scheduled pipeline executions ready to resume...");
        
        LocalDateTime now = LocalDateTime.now();
        List<PipelineExecution> scheduledExecutions = pipelineExecutionRepository.findByStatusAndResumeTimeBefore(
            ExecutionStatus.SCHEDULED, now);

        logger.info("Found {} scheduled executions ready to resume", scheduledExecutions.size());

        for (PipelineExecution execution : scheduledExecutions) {
            try {
                logger.info("Resuming scheduled pipeline execution ID: {} for flow step ID: {}", 
                           execution.getId(), execution.getFlowStepId());
                
                // Update status from SCHEDULED to RUNNING
                execution.setStatus(ExecutionStatus.RUNNING);
                execution.setStartTime(LocalDateTime.now());
                execution.setResumeTime(null); // Clear resume time
                pipelineExecutionRepository.save(execution);

                // Resume the flow execution from this step  
                // Note: This method needs to be implemented in FlowExecutionService
                // flowExecutionService.resumeFlowExecution(execution.getFlowExecutionId(), execution.getFlowStepId());
                
                logger.info("Pipeline execution ID: {} marked as RUNNING, ready for pipeline trigger", execution.getId());
                
            } catch (Exception e) {
                logger.error("Error resuming scheduled pipeline execution ID: {}", execution.getId(), e);
                // Mark as failed if resume fails
                execution.setStatus(ExecutionStatus.FAILED);
                execution.setEndTime(LocalDateTime.now());
                pipelineExecutionRepository.save(execution);
            }
        }
    }

    /**
     * Schedules a pipeline execution to run at a specific time
     */
    @Transactional
    public void schedulePipelineExecution(PipelineExecution execution, LocalDateTime resumeTime) {
        execution.setStatus(ExecutionStatus.SCHEDULED);
        execution.setResumeTime(resumeTime);
        pipelineExecutionRepository.save(execution);
        
        logger.info("Scheduled pipeline execution ID: {} to resume at: {}", execution.getId(), resumeTime);
    }
}