package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.websocket.FlowLogWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class FlowExecutionLoggingService {

    private static final Logger logger = LoggerFactory.getLogger(FlowExecutionLoggingService.class);

    @Autowired
    private FlowLogWebSocketHandler webSocketHandler;

    /**
     * Execute a task with flow execution logging context
     */
    public <T> CompletableFuture<T> executeWithLoggingContext(UUID flowExecutionId, Supplier<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            // Set up MDC context for this thread
            MDC.put("flowExecutionId", flowExecutionId.toString());
            
            try {
                logger.info("Starting flow execution task with logging context");
                
                // Send initial log message to WebSocket clients
                webSocketHandler.broadcastLogMessage(flowExecutionId.toString(), 
                    "Flow execution started - logging context initialized");
                
                T result = task.get();
                
                logger.info("Flow execution task completed successfully");
                webSocketHandler.broadcastLogMessage(flowExecutionId.toString(), 
                    "Flow execution completed successfully");
                
                return result;
                
            } catch (Exception e) {
                logger.error("Flow execution task failed", e);
                webSocketHandler.broadcastLogMessage(flowExecutionId.toString(), 
                    "Flow execution failed: " + e.getMessage());
                throw e;
            } finally {
                // Clean up MDC context
                MDC.remove("flowExecutionId");
                
                // Send final log message
                webSocketHandler.broadcastLogMessage(flowExecutionId.toString(), 
                    "Flow execution logging context closed");
            }
        });
    }

    /**
     * Execute a void task with flow execution logging context
     */
    public CompletableFuture<Void> executeWithLoggingContext(UUID flowExecutionId, Runnable task) {
        return CompletableFuture.runAsync(() -> {
            // Set up MDC context for this thread
            MDC.put("flowExecutionId", flowExecutionId.toString());
            
            try {
                logger.info("Starting flow execution task with logging context");
                
                // Send initial log message to WebSocket clients
                webSocketHandler.broadcastLogMessage(flowExecutionId.toString(), 
                    "Flow execution started - logging context initialized");
                
                task.run();
                
                logger.info("Flow execution task completed successfully");
                webSocketHandler.broadcastLogMessage(flowExecutionId.toString(), 
                    "Flow execution completed successfully");
                
            } catch (Exception e) {
                logger.error("Flow execution task failed", e);
                webSocketHandler.broadcastLogMessage(flowExecutionId.toString(), 
                    "Flow execution failed: " + e.getMessage());
                throw e;
            } finally {
                // Clean up MDC context
                MDC.remove("flowExecutionId");
                
                // Send final log message
                webSocketHandler.broadcastLogMessage(flowExecutionId.toString(), 
                    "Flow execution logging context closed");
            }
        });
    }

    /**
     * Send a custom log message to WebSocket clients for a specific flow execution
     */
    public void sendLogMessage(UUID flowExecutionId, String message) {
        webSocketHandler.broadcastLogMessage(flowExecutionId.toString(), message);
    }

    /**
     * Get the number of active WebSocket sessions for a flow execution
     */
    public int getActiveSessionCount(UUID flowExecutionId) {
        return webSocketHandler.getSessionCount(flowExecutionId.toString());
    }
}