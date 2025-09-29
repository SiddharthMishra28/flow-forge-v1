package com.testautomation.orchestrator.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@Embeddable
public class InvokeTimer {

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "timeUnit", column = @Column(name = "delay_time_unit")),
        @AttributeOverride(name = "value", column = @Column(name = "delay_value"))
    })
    @Valid
    private Delay delay;

    @Column(name = "is_scheduled")
    private Boolean isScheduled;

    @Pattern(regexp = "^[0-9*\\-,/\\s]+$", message = "Invalid cron expression format")
    @Column(name = "scheduled_cron")
    private String scheduledCron;

    // Constructors
    public InvokeTimer() {}

    public InvokeTimer(Delay delay, Boolean isScheduled, String scheduledCron) {
        this.delay = delay;
        this.isScheduled = isScheduled;
        this.scheduledCron = scheduledCron;
    }

    // Getters and Setters
    public Delay getDelay() {
        return delay;
    }

    public void setDelay(Delay delay) {
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