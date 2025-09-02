package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.squashtm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
public class SquashTmService {

    private static final Logger logger = LoggerFactory.getLogger(SquashTmService.class);

    private final WebClient webClient;

    @Value("${squashtm.base-url}")
    private String baseUrl;

    @Value("${squashtm.api-token}")
    private String apiToken;

    @Value("${squashtm.timeout:60}")
    private int timeout;

    public SquashTmService() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Test SquashTM API connectivity and return raw response
     */
    public String testApiConnectivity() {
        logger.info("Testing SquashTM API connectivity");
        logger.info("Using base URL: {}", baseUrl);
        logger.info("Using API token: {}...", apiToken != null ? apiToken.substring(0, 20) : "null");
        
        String url = String.format("%s/api/rest/latest/projects?page=0&size=1", baseUrl);
        logger.info("Test URL: {}", url);
        
        try {
            String response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeout))
                    .block();
            
            logger.info("API connectivity test successful");
            return response;
        } catch (Exception e) {
            logger.error("API connectivity test failed: {}", e.getMessage());
            throw new RuntimeException("SquashTM API connectivity test failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get all projects with pagination
     */
    public SquashProjectsResponseDto getProjects(int page, int size) {
        logger.info("Fetching SquashTM projects - page: {}, size: {}", page, size);
        logger.info("Using base URL: {}", baseUrl);
        logger.info("Using API token: {}...", apiToken != null ? apiToken.substring(0, 20) : "null");
        
        String url = String.format("%s/api/rest/latest/projects?page=%d&size=%d", baseUrl, page, size);
        logger.info("Full URL: {}", url);
        
        // First, let's get the raw response to debug
        String rawResponse = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(timeout))
                .doOnSuccess(response -> logger.info("Raw SquashTM API response: {}", response))
                .doOnError(error -> logger.error("Error fetching projects: {}", error.getMessage()))
                .onErrorMap(WebClientResponseException.class, this::handleWebClientException)
                .block();
        
        // Now try to parse it
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .retrieve()
                .bodyToMono(SquashProjectsResponseDto.class)
                .timeout(Duration.ofSeconds(timeout))
                .doOnSuccess(response -> logger.info("Successfully fetched {} projects", 
                    response.getEmbedded() != null && response.getEmbedded().getProjects() != null ? 
                    response.getEmbedded().getProjects().size() : 0))
                .doOnError(error -> logger.error("Error parsing projects response: {}", error.getMessage()))
                .onErrorMap(WebClientResponseException.class, this::handleWebClientException)
                .block();
    }

    /**
     * Get test case folders with pagination
     */
    public SquashPagedResponseDto<SquashTestCaseFolderDto> getTestCaseFolders(int page, int size) {
        logger.info("Fetching SquashTM test case folders - page: {}, size: {}", page, size);
        
        String url = String.format("%s/api/rest/latest/test-case-folders?page=%d&size=%d", baseUrl, page, size);
        
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SquashPagedResponseDto<SquashTestCaseFolderDto>>() {})
                .timeout(Duration.ofSeconds(timeout))
                .doOnSuccess(response -> logger.debug("Successfully fetched test case folders"))
                .doOnError(error -> logger.error("Error fetching test case folders: {}", error.getMessage()))
                .onErrorMap(WebClientResponseException.class, this::handleWebClientException)
                .block();
    }

    /**
     * Get test cases with pagination and optional fields
     */
    public SquashPagedResponseDto<SquashTestCaseDto> getTestCases(int page, int size, String fields) {
        logger.info("Fetching SquashTM test cases - page: {}, size: {}, fields: {}", page, size, fields);
        
        String url = String.format("%s/api/rest/latest/test-cases?page=%d&size=%d", baseUrl, page, size);
        if (fields != null && !fields.trim().isEmpty()) {
            url += "&fields=" + fields;
        }
        
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SquashPagedResponseDto<SquashTestCaseDto>>() {})
                .timeout(Duration.ofSeconds(timeout))
                .doOnSuccess(response -> logger.debug("Successfully fetched test cases"))
                .doOnError(error -> logger.error("Error fetching test cases: {}", error.getMessage()))
                .onErrorMap(WebClientResponseException.class, this::handleWebClientException)
                .block();
    }

    /**
     * Get test case by ID
     */
    public SquashTestCaseDto getTestCaseById(Long testCaseId) {
        logger.info("Fetching SquashTM test case by ID: {}", testCaseId);
        
        String url = String.format("%s/api/rest/latest/test-cases/%d", baseUrl, testCaseId);
        
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .retrieve()
                .bodyToMono(SquashTestCaseDto.class)
                .timeout(Duration.ofSeconds(timeout))
                .doOnSuccess(response -> logger.debug("Successfully fetched test case: {}", response.getName()))
                .doOnError(error -> logger.error("Error fetching test case {}: {}", testCaseId, error.getMessage()))
                .onErrorMap(WebClientResponseException.class, this::handleWebClientException)
                .block();
    }

    /**
     * Get test steps for a specific test case
     */
    public List<SquashTestStepDto> getTestSteps(Long testCaseId) {
        logger.info("Fetching SquashTM test steps for test case: {}", testCaseId);
        
        String url = String.format("%s/api/rest/latest/test-cases/%d/steps", baseUrl, testCaseId);
        
        SquashPagedResponseDto<SquashTestStepDto> response = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SquashPagedResponseDto<SquashTestStepDto>>() {})
                .timeout(Duration.ofSeconds(timeout))
                .doOnSuccess(res -> logger.debug("Successfully fetched test steps for test case: {}", testCaseId))
                .doOnError(error -> logger.error("Error fetching test steps for test case {}: {}", testCaseId, error.getMessage()))
                .onErrorMap(WebClientResponseException.class, this::handleWebClientException)
                .block();
        
        return response != null && response.getEmbedded() != null ? response.getEmbedded().getData() : List.of();
    }

    /**
     * Get project by ID
     */
    public SquashProjectDto getProjectById(Long projectId) {
        logger.info("Fetching SquashTM project by ID: {}", projectId);
        
        String url = String.format("%s/api/rest/latest/projects/%d", baseUrl, projectId);
        
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .retrieve()
                .bodyToMono(SquashProjectDto.class)
                .timeout(Duration.ofSeconds(timeout))
                .doOnSuccess(response -> logger.debug("Successfully fetched project: {}", response.getName()))
                .doOnError(error -> logger.error("Error fetching project {}: {}", projectId, error.getMessage()))
                .onErrorMap(WebClientResponseException.class, this::handleWebClientException)
                .block();
    }

    /**
     * Get test case folder by ID
     */
    public SquashTestCaseFolderDto getTestCaseFolderById(Long folderId) {
        logger.info("Fetching SquashTM test case folder by ID: {}", folderId);
        
        String url = String.format("%s/api/rest/latest/test-case-folders/%d", baseUrl, folderId);
        
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .retrieve()
                .bodyToMono(SquashTestCaseFolderDto.class)
                .timeout(Duration.ofSeconds(timeout))
                .doOnSuccess(response -> logger.debug("Successfully fetched test case folder: {}", response.getName()))
                .doOnError(error -> logger.error("Error fetching test case folder {}: {}", folderId, error.getMessage()))
                .onErrorMap(WebClientResponseException.class, this::handleWebClientException)
                .block();
    }

    private RuntimeException handleWebClientException(WebClientResponseException ex) {
        logger.error("SquashTM API error - Status: {}, Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        logger.error("Request headers that were sent: Authorization: Bearer {}...", apiToken != null ? apiToken.substring(0, 20) : "null");
        return new RuntimeException("SquashTM API error: " + ex.getMessage(), ex);
    }
}