package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.FlowStepDto;
import com.testautomation.orchestrator.model.FlowStep;
import com.testautomation.orchestrator.repository.ApplicationRepository;
import com.testautomation.orchestrator.repository.FlowStepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlowStepService {

    private static final Logger logger = LoggerFactory.getLogger(FlowStepService.class);

    @Autowired
    private FlowStepRepository flowStepRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public FlowStepDto createFlowStep(FlowStepDto flowStepDto) {
        logger.info("Creating new flow step for application ID: {}", flowStepDto.getApplicationId());
        
        // Validate that the application exists
        if (!applicationRepository.existsById(flowStepDto.getApplicationId())) {
            throw new IllegalArgumentException("Application not found with ID: " + flowStepDto.getApplicationId());
        }
        
        FlowStep flowStep = convertToEntity(flowStepDto);
        FlowStep savedFlowStep = flowStepRepository.save(flowStep);
        
        logger.info("Flow step created with ID: {}", savedFlowStep.getId());
        return convertToDto(savedFlowStep);
    }

    @Transactional(readOnly = true)
    public Optional<FlowStepDto> getFlowStepById(Long id) {
        logger.debug("Fetching flow step with ID: {}", id);
        return flowStepRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<FlowStepDto> getAllFlowSteps() {
        logger.debug("Fetching all flow steps");
        return flowStepRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FlowStepDto> getFlowStepsByApplicationId(Long applicationId) {
        logger.debug("Fetching flow steps for application ID: {}", applicationId);
        return flowStepRepository.findByApplicationId(applicationId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FlowStepDto> getFlowStepsByIds(List<Long> ids) {
        logger.debug("Fetching flow steps by IDs: {}", ids);
        return flowStepRepository.findByIdIn(ids)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public FlowStepDto updateFlowStep(Long id, FlowStepDto flowStepDto) {
        logger.info("Updating flow step with ID: {}", id);
        
        FlowStep existingFlowStep = flowStepRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flow step not found with ID: " + id));
        
        // Validate that the application exists if it's being changed
        if (!existingFlowStep.getApplicationId().equals(flowStepDto.getApplicationId()) &&
            !applicationRepository.existsById(flowStepDto.getApplicationId())) {
            throw new IllegalArgumentException("Application not found with ID: " + flowStepDto.getApplicationId());
        }
        
        existingFlowStep.setApplicationId(flowStepDto.getApplicationId());
        existingFlowStep.setBranch(flowStepDto.getBranch());
        existingFlowStep.setTestTag(flowStepDto.getTestTag());
        existingFlowStep.setTestStage(flowStepDto.getTestStage());
        existingFlowStep.setSquashStepIds(flowStepDto.getSquashStepIds());
        existingFlowStep.setInitialTestData(flowStepDto.getInitialTestData());
        
        FlowStep updatedFlowStep = flowStepRepository.save(existingFlowStep);
        
        logger.info("Flow step updated successfully with ID: {}", updatedFlowStep.getId());
        return convertToDto(updatedFlowStep);
    }

    public void deleteFlowStep(Long id) {
        logger.info("Deleting flow step with ID: {}", id);
        
        if (!flowStepRepository.existsById(id)) {
            throw new IllegalArgumentException("Flow step not found with ID: " + id);
        }
        
        flowStepRepository.deleteById(id);
        logger.info("Flow step deleted successfully with ID: {}", id);
    }

    private FlowStep convertToEntity(FlowStepDto dto) {
        FlowStep flowStep = new FlowStep();
        flowStep.setApplicationId(dto.getApplicationId());
        flowStep.setBranch(dto.getBranch());
        flowStep.setTestTag(dto.getTestTag());
        flowStep.setTestStage(dto.getTestStage());
        flowStep.setSquashStepIds(dto.getSquashStepIds());
        flowStep.setInitialTestData(dto.getInitialTestData());
        return flowStep;
    }

    private FlowStepDto convertToDto(FlowStep entity) {
        FlowStepDto dto = new FlowStepDto();
        dto.setId(entity.getId());
        dto.setApplicationId(entity.getApplicationId());
        dto.setBranch(entity.getBranch());
        dto.setTestTag(entity.getTestTag());
        dto.setTestStage(entity.getTestStage());
        dto.setSquashStepIds(entity.getSquashStepIds());
        dto.setInitialTestData(entity.getInitialTestData());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}