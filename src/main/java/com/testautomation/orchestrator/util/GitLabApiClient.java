package com.testautomation.orchestrator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GitLabApiClient {

    private static final Logger logger = LoggerFactory.getLogger(GitLabApiClient.class);
    private final WebClient webClient;

    public GitLabApiClient() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    /**
     * Trigger a GitLab pipeline execution
     */
    public Mono<GitLabPipelineResponse> triggerPipeline(String gitlabBaseUrl, String projectId, 
                                                       String branch, String accessToken, 
                                                       Map<String, String> variables) {
        String url = String.format("%s/api/v4/projects/%s/pipeline", gitlabBaseUrl, projectId);
        
        logger.info("Triggering GitLab pipeline for project {} on branch {}", projectId, branch);
        
        GitLabPipelineRequest request = new GitLabPipelineRequest(branch, variables);
        
        return webClient.post()
                .uri(url)
                .header("PRIVATE-TOKEN", accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                         response -> response.bodyToMono(String.class)
                                 .doOnNext(body -> logger.error("GitLab API error response: {}", body))
                                 .then(Mono.error(new RuntimeException("GitLab API error: " + response.statusCode()))))
                .bodyToMono(GitLabPipelineResponse.class)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(response -> logger.info("Pipeline triggered successfully: {}", response.getId()))
                .doOnError(error -> logger.error("Failed to trigger pipeline: {}", error.getMessage()));
    }

    /**
     * Get pipeline status
     */
    public Mono<GitLabPipelineResponse> getPipelineStatus(String gitlabBaseUrl, String projectId, 
                                                         Long pipelineId, String accessToken) {
        String url = String.format("%s/api/v4/projects/%s/pipelines/%d", gitlabBaseUrl, projectId, pipelineId);
        
        return webClient.get()
                .uri(url)
                .header("PRIVATE-TOKEN", accessToken)
                .retrieve()
                .bodyToMono(GitLabPipelineResponse.class)
                .timeout(Duration.ofSeconds(15))
                .doOnError(error -> logger.error("Failed to get pipeline status: {}", error.getMessage()));
    }

    /**
     * Get jobs for a pipeline
     */
    public Mono<GitLabJobsResponse[]> getPipelineJobs(String gitlabBaseUrl, String projectId, 
                                                     Long pipelineId, String accessToken) {
        String url = String.format("%s/api/v4/projects/%s/pipelines/%d/jobs", 
                                  gitlabBaseUrl, projectId, pipelineId);
        
        logger.debug("Getting jobs for pipeline {}", pipelineId);
        
        return webClient.get()
                .uri(url)
                .header("PRIVATE-TOKEN", accessToken)
                .retrieve()
                .bodyToMono(GitLabJobsResponse[].class)
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> logger.error("Failed to get pipeline jobs: {}", error.getMessage()));
    }

    /**
     * Download specific artifact from a job
     */
    public Mono<String> downloadJobArtifact(String gitlabBaseUrl, String projectId, 
                                           Long jobId, String accessToken, String artifactPath) {
        String url = String.format("%s/api/v4/projects/%s/jobs/%d/artifacts/%s", 
                                  gitlabBaseUrl, projectId, jobId, artifactPath);
        
        logger.info("Downloading artifact {} from job {}", artifactPath, jobId);
        
        return webClient.get()
                .uri(url)
                .header("PRIVATE-TOKEN", accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                .doOnSuccess(content -> logger.info("Artifact downloaded successfully from job {}", jobId))
                .doOnError(error -> logger.debug("Failed to download artifact from job {}: {}", jobId, error.getMessage()));
    }

    // Inner classes for GitLab API request/response
    public static class GitLabPipelineRequest {
        private String ref;
        private List<GitLabVariable> variables;

        public GitLabPipelineRequest() {}

        public GitLabPipelineRequest(String ref, Map<String, String> variablesMap) {
            this.ref = ref;
            this.variables = new ArrayList<>();
            if (variablesMap != null) {
                variablesMap.forEach((key, value) -> 
                    this.variables.add(new GitLabVariable(key, value)));
            }
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public List<GitLabVariable> getVariables() {
            return variables;
        }

        public void setVariables(List<GitLabVariable> variables) {
            this.variables = variables;
        }
    }

    public static class GitLabVariable {
        private String key;
        private String value;

        public GitLabVariable() {}

        public GitLabVariable(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class GitLabPipelineResponse {
        private Long id;
        private String status;
        private String ref;
        
        @com.fasterxml.jackson.annotation.JsonProperty("web_url")
        private String webUrl;
        
        @com.fasterxml.jackson.annotation.JsonProperty("created_at")
        private String createdAt;
        
        @com.fasterxml.jackson.annotation.JsonProperty("updated_at")
        private String updatedAt;

        public GitLabPipelineResponse() {}

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getWebUrl() {
            return webUrl;
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public boolean isCompleted() {
            return "success".equals(status) || "failed".equals(status) || "canceled".equals(status);
        }

        public boolean isSuccessful() {
            return "success".equals(status);
        }
    }

    public static class GitLabJobsResponse {
        private Long id;
        private String name;
        private String stage;
        private String status;
        
        @com.fasterxml.jackson.annotation.JsonProperty("web_url")
        private String webUrl;
        
        @com.fasterxml.jackson.annotation.JsonProperty("created_at")
        private String createdAt;
        
        @com.fasterxml.jackson.annotation.JsonProperty("started_at")
        private String startedAt;
        
        @com.fasterxml.jackson.annotation.JsonProperty("finished_at")
        private String finishedAt;

        public GitLabJobsResponse() {}

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStage() {
            return stage;
        }

        public void setStage(String stage) {
            this.stage = stage;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getWebUrl() {
            return webUrl;
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getStartedAt() {
            return startedAt;
        }

        public void setStartedAt(String startedAt) {
            this.startedAt = startedAt;
        }

        public String getFinishedAt() {
            return finishedAt;
        }

        public void setFinishedAt(String finishedAt) {
            this.finishedAt = finishedAt;
        }

        public boolean isCompleted() {
            return "success".equals(status) || "failed".equals(status) || "canceled".equals(status);
        }

        public boolean isSuccessful() {
            return "success".equals(status);
        }
    }
}