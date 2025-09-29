package com.testautomation.orchestrator.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FlowLogWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(FlowLogWebSocketHandler.class);
    
    // Map to store WebSocket sessions by flow execution ID
    private final Map<String, CopyOnWriteArraySet<WebSocketSession>> flowExecutionSessions = new ConcurrentHashMap<>();
    
    // Pattern to extract flow execution ID from WebSocket path
    private static final Pattern FLOW_EXECUTION_ID_PATTERN = Pattern.compile("/ws/flow-logs/([^/]+)");

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String flowExecutionId = extractFlowExecutionId(session);
        if (flowExecutionId != null) {
            flowExecutionSessions.computeIfAbsent(flowExecutionId, k -> new CopyOnWriteArraySet<>()).add(session);
            logger.info("WebSocket connection established for flow execution: {} (Session: {})", flowExecutionId, session.getId());
            
            // Send initial welcome message
            sendMessage(session, "Connected to flow execution logs: " + flowExecutionId);
        } else {
            logger.warn("Invalid WebSocket path, closing connection: {}", session.getUri());
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String flowExecutionId = extractFlowExecutionId(session);
        if (flowExecutionId != null) {
            CopyOnWriteArraySet<WebSocketSession> sessions = flowExecutionSessions.get(flowExecutionId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    flowExecutionSessions.remove(flowExecutionId);
                }
            }
            logger.info("WebSocket connection closed for flow execution: {} (Session: {})", flowExecutionId, session.getId());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport error for session: {}", session.getId(), exception);
        String flowExecutionId = extractFlowExecutionId(session);
        if (flowExecutionId != null) {
            CopyOnWriteArraySet<WebSocketSession> sessions = flowExecutionSessions.get(flowExecutionId);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
    }

    /**
     * Broadcast log message to all sessions connected to a specific flow execution
     */
    public void broadcastLogMessage(String flowExecutionId, String logMessage) {
        CopyOnWriteArraySet<WebSocketSession> sessions = flowExecutionSessions.get(flowExecutionId);
        if (sessions != null && !sessions.isEmpty()) {
            sessions.removeIf(session -> {
                try {
                    if (session.isOpen()) {
                        sendMessage(session, logMessage);
                        return false;
                    } else {
                        return true; // Remove closed sessions
                    }
                } catch (Exception e) {
                    logger.error("Error sending log message to WebSocket session: {}", session.getId(), e);
                    return true; // Remove problematic sessions
                }
            });
        }
    }

    /**
     * Get count of active sessions for a flow execution
     */
    public int getSessionCount(String flowExecutionId) {
        CopyOnWriteArraySet<WebSocketSession> sessions = flowExecutionSessions.get(flowExecutionId);
        return sessions != null ? sessions.size() : 0;
    }

    private String extractFlowExecutionId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri != null) {
            Matcher matcher = FLOW_EXECUTION_ID_PATTERN.matcher(uri.getPath());
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private void sendMessage(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            logger.error("Failed to send WebSocket message to session: {}", session.getId(), e);
        }
    }
}