package com.testautomation.orchestrator.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.testautomation.orchestrator.websocket.FlowLogWebSocketHandler;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FlowExecutionLogAppender extends AppenderBase<ILoggingEvent> {

    private static FlowLogWebSocketHandler webSocketHandler;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Autowired
    public void setWebSocketHandler(FlowLogWebSocketHandler webSocketHandler) {
        FlowExecutionLogAppender.webSocketHandler = webSocketHandler;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (webSocketHandler == null) {
            return;
        }

        // Check if this log event is from a flow execution thread
        String flowExecutionId = MDC.get("flowExecutionId");
        String threadName = event.getThreadName();
        
        // Only process logs that have flowExecutionId in MDC or are from flow execution threads
        if (flowExecutionId != null || (threadName != null && threadName.contains("flow-execution"))) {
            try {
                String formattedMessage = formatLogMessage(event, flowExecutionId);
                
                if (flowExecutionId != null) {
                    webSocketHandler.broadcastLogMessage(flowExecutionId, formattedMessage);
                } else {
                    // Try to extract flow execution ID from thread name
                    String extractedFlowId = extractFlowIdFromThreadName(threadName);
                    if (extractedFlowId != null) {
                        webSocketHandler.broadcastLogMessage(extractedFlowId, formattedMessage);
                    }
                }
            } catch (Exception e) {
                // Avoid infinite loop by not logging this error
                System.err.println("Error in FlowExecutionLogAppender: " + e.getMessage());
            }
        }
    }

    private String formatLogMessage(ILoggingEvent event, String flowExecutionId) {
        StringBuilder sb = new StringBuilder();
        
        // Timestamp
        sb.append(LocalDateTime.now().format(TIMESTAMP_FORMAT));
        sb.append(" ");
        
        // Log level
        sb.append("[").append(event.getLevel()).append("]");
        sb.append(" ");
        
        // Thread name
        if (event.getThreadName() != null) {
            sb.append("[").append(event.getThreadName()).append("]");
            sb.append(" ");
        }
        
        // Logger name (shortened)
        String loggerName = event.getLoggerName();
        if (loggerName != null) {
            String[] parts = loggerName.split("\\.");
            if (parts.length > 0) {
                sb.append(parts[parts.length - 1]);
            }
            sb.append(" - ");
        }
        
        // Message
        sb.append(event.getFormattedMessage());
        
        // Flow execution ID (if available and not already in message)
        if (flowExecutionId != null && !event.getFormattedMessage().contains(flowExecutionId)) {
            sb.append(" [FlowId: ").append(flowExecutionId).append("]");
        }
        
        return sb.toString();
    }

    private String extractFlowIdFromThreadName(String threadName) {
        if (threadName != null && threadName.contains("flow-execution-")) {
            try {
                // Expected format: "flow-execution-{flowExecutionId}-{threadNumber}"
                String[] parts = threadName.split("-");
                if (parts.length >= 3) {
                    return parts[2]; // Extract the UUID part
                }
            } catch (Exception e) {
                // Ignore extraction errors
            }
        }
        return null;
    }
}