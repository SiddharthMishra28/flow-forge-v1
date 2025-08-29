package com.testautomation.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PipelineOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PipelineOrchestratorApplication.class, args);
    }
}