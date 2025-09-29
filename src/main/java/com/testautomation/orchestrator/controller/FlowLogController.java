package com.testautomation.orchestrator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flow-executions")
@Tag(name = "Flow Executions", description = "Flow Execution Management API")
public class FlowLogController {

    private static final Logger logger = LoggerFactory.getLogger(FlowLogController.class);

    @GetMapping(value = "/{flowExecutionId}/live-logs", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Get live logs HTML page", description = "Serve HTML page for real-time flow execution logs")
    @ApiResponse(responseCode = "200", description = "HTML page served successfully")
    public ResponseEntity<String> getLiveLogsPage(
            @Parameter(description = "Flow execution ID") @PathVariable String flowExecutionId) {
        
        logger.debug("Serving live logs page for flow execution: {}", flowExecutionId);
        
        String htmlContent = generateLiveLogsHtml(flowExecutionId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }

    private String generateLiveLogsHtml(String flowExecutionId) {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Live Logs - Flow Execution %s</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Courier New', monospace;
            background-color: #1e1e1e;
            color: #d4d4d4;
            height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .header {
            background-color: #2d2d30;
            padding: 15px 20px;
            border-bottom: 1px solid #3e3e42;
            box-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }
        
        .header h1 {
            color: #ffffff;
            font-size: 24px;
            margin-bottom: 5px;
        }
        
        .header .flow-id {
            color: #0078d4;
            font-size: 14px;
            font-weight: normal;
        }
        
        .controls {
            background-color: #252526;
            padding: 10px 20px;
            border-bottom: 1px solid #3e3e42;
            display: flex;
            gap: 10px;
            align-items: center;
        }
        
        .status {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .status-indicator {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background-color: #dc3545;
        }
        
        .status-indicator.connected {
            background-color: #28a745;
        }
        
        .status-text {
            font-size: 14px;
            color: #d4d4d4;
        }
        
        .controls button {
            background-color: #0078d4;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        
        .controls button:hover {
            background-color: #106ebe;
        }
        
        .controls button:disabled {
            background-color: #3e3e42;
            cursor: not-allowed;
        }
        
        .log-container {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            background-color: #1e1e1e;
        }
        
        .log-entry {
            margin-bottom: 4px;
            line-height: 1.4;
            font-size: 14px;
            white-space: pre-wrap;
            word-wrap: break-word;
        }
        
        .log-entry.info {
            color: #d4d4d4;
        }
        
        .log-entry.warn {
            color: #ffcc02;
        }
        
        .log-entry.error {
            color: #f14c4c;
        }
        
        .log-entry.debug {
            color: #9cdcfe;
        }
        
        .log-entry.trace {
            color: #808080;
        }
        
        .timestamp {
            color: #6a9955;
        }
        
        .level {
            font-weight: bold;
        }
        
        .thread {
            color: #d7ba7d;
        }
        
        .logger {
            color: #4ec9b0;
        }
        
        .flow-id-tag {
            color: #0078d4;
            font-weight: bold;
        }
        
        .no-logs {
            text-align: center;
            color: #808080;
            margin-top: 50px;
            font-style: italic;
        }
        
        .auto-scroll {
            margin-left: auto;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .auto-scroll input[type="checkbox"] {
            transform: scale(1.2);
        }
        
        ::-webkit-scrollbar {
            width: 12px;
        }
        
        ::-webkit-scrollbar-track {
            background: #2d2d30;
        }
        
        ::-webkit-scrollbar-thumb {
            background: #464647;
            border-radius: 6px;
        }
        
        ::-webkit-scrollbar-thumb:hover {
            background: #5a5a5c;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Live Server Logs</h1>
        <div class="flow-id">Flow Execution: %s</div>
    </div>
    
    <div class="controls">
        <div class="status">
            <div class="status-indicator" id="statusIndicator"></div>
            <span class="status-text" id="statusText">Disconnected</span>
        </div>
        <button id="connectBtn" onclick="connect()">Connect</button>
        <button id="clearBtn" onclick="clearLogs()">Clear Logs</button>
        <div class="auto-scroll">
            <input type="checkbox" id="autoScroll" checked>
            <label for="autoScroll">Auto-scroll</label>
        </div>
    </div>
    
    <div class="log-container" id="logContainer">
        <div class="no-logs">Connecting to live logs...</div>
    </div>

    <script>
        let socket = null;
        let logCount = 0;
        const maxLogs = 1000; // Limit to prevent memory issues
        const flowExecutionId = '%s';
        
        const statusIndicator = document.getElementById('statusIndicator');
        const statusText = document.getElementById('statusText');
        const connectBtn = document.getElementById('connectBtn');
        const logContainer = document.getElementById('logContainer');
        const autoScrollCheckbox = document.getElementById('autoScroll');
        
        function connect() {
            if (socket && socket.readyState === WebSocket.OPEN) {
                socket.close();
                return;
            }
            
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = `${protocol}//${window.location.host}/ws/flow-logs/${flowExecutionId}`;
            
            socket = new WebSocket(wsUrl);
            
            socket.onopen = function(event) {
                updateConnectionStatus(true);
                connectBtn.textContent = 'Disconnect';
                logContainer.innerHTML = '';
                logCount = 0;
            };
            
            socket.onmessage = function(event) {
                addLogEntry(event.data);
            };
            
            socket.onclose = function(event) {
                updateConnectionStatus(false);
                connectBtn.textContent = 'Connect';
            };
            
            socket.onerror = function(error) {
                console.error('WebSocket error:', error);
                updateConnectionStatus(false);
                connectBtn.textContent = 'Connect';
            };
        }
        
        function updateConnectionStatus(connected) {
            if (connected) {
                statusIndicator.classList.add('connected');
                statusText.textContent = 'Connected';
            } else {
                statusIndicator.classList.remove('connected');
                statusText.textContent = 'Disconnected';
            }
        }
        
        function addLogEntry(message) {
            if (logCount >= maxLogs) {
                // Remove oldest log entries to prevent memory issues
                const firstLog = logContainer.querySelector('.log-entry');
                if (firstLog) {
                    firstLog.remove();
                    logCount--;
                }
            }
            
            const logEntry = document.createElement('div');
            logEntry.className = 'log-entry';
            
            // Determine log level and apply appropriate styling
            const lowerMessage = message.toLowerCase();
            if (lowerMessage.includes('[error]')) {
                logEntry.classList.add('error');
            } else if (lowerMessage.includes('[warn]')) {
                logEntry.classList.add('warn');
            } else if (lowerMessage.includes('[debug]')) {
                logEntry.classList.add('debug');
            } else if (lowerMessage.includes('[trace]')) {
                logEntry.classList.add('trace');
            } else {
                logEntry.classList.add('info');
            }
            
            // Format the message with syntax highlighting
            logEntry.innerHTML = formatLogMessage(message);
            
            logContainer.appendChild(logEntry);
            logCount++;
            
            // Auto-scroll to bottom if enabled
            if (autoScrollCheckbox.checked) {
                logContainer.scrollTop = logContainer.scrollHeight;
            }
        }
        
        function formatLogMessage(message) {
            // Simple syntax highlighting for log components
            return message
                .replace(/^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})/, '<span class="timestamp">$1</span>')
                .replace(/\\[(ERROR|WARN|INFO|DEBUG|TRACE)\\]/, '<span class="level">[$1]</span>')
                .replace(/\\[([^\\]]+)\\]/g, (match, content) => {
                    if (content.includes('flow-execution') || content.includes('FlowId:')) {
                        return `<span class="flow-id-tag">[${content}]</span>`;
                    }
                    return `<span class="thread">[${content}]</span>`;
                })
                .replace(/([A-Za-z]+Service|[A-Za-z]+Controller|[A-Za-z]+Repository) -/, '<span class="logger">$1</span> -');
        }
        
        function clearLogs() {
            logContainer.innerHTML = '<div class="no-logs">Logs cleared. Waiting for new logs...</div>';
            logCount = 0;
        }
        
        // Auto-connect on page load
        window.onload = function() {
            connect();
        };
        
        // Handle page unload
        window.onbeforeunload = function() {
            if (socket) {
                socket.close();
            }
        };
    </script>
</body>
</html>""".formatted(flowExecutionId, flowExecutionId, flowExecutionId);
    }
}