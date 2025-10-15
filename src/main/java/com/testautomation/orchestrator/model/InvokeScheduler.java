package com.testautomation.orchestrator.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Embeddable
public class InvokeScheduler {

    @NotNull
    @Pattern(regexp = "^(scheduled|delayed)$", message = "Type must be either 'scheduled' or 'delayed'")
    @Column(name = "scheduler_type", nullable = false)
    private String type;

    @Embedded
    @Valid
    @NotNull
    private Timer timer;

    // Constructors
    public InvokeScheduler() {}

    public InvokeScheduler(String type, Timer timer) {
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

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}