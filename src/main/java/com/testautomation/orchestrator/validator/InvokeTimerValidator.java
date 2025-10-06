package com.testautomation.orchestrator.validator;

import com.testautomation.orchestrator.dto.InvokeTimerDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InvokeTimerValidator implements ConstraintValidator<ValidInvokeTimer, InvokeTimerDto> {

    @Override
    public void initialize(ValidInvokeTimer constraintAnnotation) {
    }

    @Override
    public boolean isValid(InvokeTimerDto invokeTimerDto, ConstraintValidatorContext context) {
        if (invokeTimerDto == null) {
            return true; // Null is valid
        }

        boolean hasDelay = invokeTimerDto.getDelay() != null;
        boolean hasScheduledCron = invokeTimerDto.getScheduledCron() != null && !invokeTimerDto.getScheduledCron().isBlank();

        if (hasDelay && hasScheduledCron) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("A FlowStep cannot be both delayed and scheduled with cron.")
                   .addPropertyNode("delay").addConstraintViolation();
            return false;
        }

        if (!hasDelay && !hasScheduledCron) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Either delay or scheduledCron must be populated when invokeTimer is provided.")
                   .addPropertyNode("delay").addConstraintViolation();
            return false;
        }

        return true;
    }
}
