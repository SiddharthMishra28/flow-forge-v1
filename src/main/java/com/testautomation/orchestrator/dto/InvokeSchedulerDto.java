package com.testautomation.orchestrator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import com.testautomation.orchestrator.validator.ValidInvokeScheduler;
import io.swagger.v3.oas.annotations.media.Schema;

@ValidInvokeScheduler
public class InvokeSchedulerDto {

    @NotNull(message = "Type is required")
    @Pattern(regexp = "^(scheduled|delayed)$", message = "Type must be either 'scheduled' or 'delayed'")
    @Schema(description = "Scheduler type - 'scheduled' for absolute time scheduling, 'delayed' for relative delay", 
            example = "scheduled", allowableValues = {"scheduled", "delayed"})
    private String type;

    @Valid
    @NotNull(message = "Timer configuration is required")
    @Schema(description = "Timer configuration object")
    private TimerDto timer;

    // Constructors
    public InvokeSchedulerDto() {}

    public InvokeSchedulerDto(String type, TimerDto timer) {
        this.type = type;
        this.timer = timer;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TimerDto getTimer() {
        return timer;
    }

    public void setTimer(TimerDto timer) {
        this.timer = timer;
    }
}