package com.testautomation.orchestrator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

public class InvokeTimerDto {

    @Valid
    @Schema(description = "Delay configuration for the flow step")
    private DelayDto delay;

    @Schema(description = "Whether the step should be scheduled based on cron expression", example = "true")
    private Boolean isScheduled;

    @Pattern(regexp = "^[0-9*\\-,/\\s]+$", message = "Invalid cron expression format")
    @Schema(description = "Cron expression for scheduling (used when isScheduled is true)", 
            example = "0 0 2 * * *", 
            pattern = "Standard cron format: second minute hour day month dayOfWeek")
    private String scheduledCron;

    // Constructors
    public InvokeTimerDto() {}

    public InvokeTimerDto(DelayDto delay, Boolean isScheduled, String scheduledCron) {
        this.delay = delay;
        this.isScheduled = isScheduled;
        this.scheduledCron = scheduledCron;
    }

    // Getters and Setters
    public DelayDto getDelay() {
        return delay;
    }

    public void setDelay(DelayDto delay) {
        this.delay = delay;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public String getScheduledCron() {
        return scheduledCron;
    }

    public void setScheduledCron(String scheduledCron) {
        this.scheduledCron = scheduledCron;
    }
}