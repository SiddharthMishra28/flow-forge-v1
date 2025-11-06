package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.CombinedFlowStepDto;
import com.testautomation.orchestrator.dto.TestDataDto;
import com.testautomation.orchestrator.model.FlowStep;
import com.testautomation.orchestrator.repository.ApplicationRepository;
import com.testautomation.orchestrator.repository.FlowStepRepository;
import com.testautomation.orchestrator.repository.TestDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CombinedFlowStepService {

    private static final Logger logger = LoggerFactory.getLogger(CombinedFlowStepService.class);

    @Autowired
    private FlowStepRepository flowStepRepository;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private TestDataService testDataService;

    public CombinedFlowStepDto createFlowStep(CombinedFlowStepDto flowStepDto) {
        logger.info("Creating new flow step for application ID: {}", flowStepDto.getApplicationId());
        
        // Validate that the application exists
        if (!applicationRepository.existsById(flowStepDto.getApplicationId())) {
            throw new IllegalArgumentException("Application not found with ID: " + flowStepDto.getApplicationId());
        }
        
        // Create test data entries first
        List<Long> testDataIds = createTestDataEntries(flowStepDto.getTestData());
        
        // Create flow step
        FlowStep flowStep = new FlowStep();
        flowStep.setApplicationId(flowStepDto.getApplicationId());
        flowStep.setBranch(flowStepDto.getBranch());
        flowStep.setTestTag(flowStepDto.getTestTag());
        flowStep.setTestStage(flowStepDto.getTestStage());
        flowStep.setDescription(flowStepDto.getDescription());
        flowStep.setSquashStepIds(flowStepDto.getSquashStepIds());
        flowStep.setTestDataIds(testDataIds);
        
        // Handle optional invokeScheduler
        if (flowStepDto.getInvokeScheduler() != null) {
            flowStep.setInvokeScheduler(convertInvokeSchedulerDtoToEntity(flowStepDto.getInvokeScheduler()));
        }
        
        FlowStep savedFlowStep = flowStepRepository.save(flowStep);
        
        logger.info("Flow step created with ID: {}", savedFlowStep.getId());
        return convertToDto(savedFlowStep);
    }

    @Transactional(readOnly = true)
    public Optional<CombinedFlowStepDto> getFlowStepById(Long id) {
        logger.debug("Fetching flow step with ID: {}", id);
        return flowStepRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<CombinedFlowStepDto> getAllFlowSteps() {
        logger.debug("Fetching all flow steps");
        return flowStepRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CombinedFlowStepDto> getAllFlowSteps(Pageable pageable) {
        logger.debug("Fetching all flow steps with pagination: {}", pageable);
        return flowStepRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<CombinedFlowStepDto> getFlowStepsByApplicationId(Long applicationId) {
        logger.debug("Fetching flow steps for application ID: {}", applicationId);
        return flowStepRepository.findByApplicationId(applicationId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CombinedFlowStepDto> getFlowStepsByApplicationId(Long applicationId, Pageable pageable) {
        logger.debug("Fetching flow steps for application ID: {} with pagination: {}", applicationId, pageable);
        return flowStepRepository.findByApplicationId(applicationId, pageable)
                .map(this::convertToDto);
    }

    public CombinedFlowStepDto updateFlowStep(Long id, CombinedFlowStepDto flowStepDto) {
        logger.info("Updating flow step with ID: {}", id);
        
        FlowStep existingFlowStep = flowStepRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flow step not found with ID: " + id));
        
        // Validate that the application exists if it's being changed
        if (!existingFlowStep.getApplicationId().equals(flowStepDto.getApplicationId()) &&
            !applicationRepository.existsById(flowStepDto.getApplicationId())) {
            throw new IllegalArgumentException("Application not found with ID: " + flowStepDto.getApplicationId());
        }
        
        // Delete old test data
        if (existingFlowStep.getTestDataIds() != null && !existingFlowStep.getTestDataIds().isEmpty()) {
            testDataRepository.deleteByDataIdIn(existingFlowStep.getTestDataIds());
        }
        
        // Create new test data entries
        List<Long> newTestDataIds = createTestDataEntries(flowStepDto.getTestData());
        
        // Update flow step
        existingFlowStep.setApplicationId(flowStepDto.getApplicationId());
        existingFlowStep.setBranch(flowStepDto.getBranch());
        existingFlowStep.setTestTag(flowStepDto.getTestTag());
        existingFlowStep.setTestStage(flowStepDto.getTestStage());
        existingFlowStep.setDescription(flowStepDto.getDescription());
        existingFlowStep.setSquashStepIds(flowStepDto.getSquashStepIds());
        existingFlowStep.setTestDataIds(newTestDataIds);
        
        // Handle optional invokeScheduler
        if (flowStepDto.getInvokeScheduler() != null) {
            existingFlowStep.setInvokeScheduler(convertInvokeSchedulerDtoToEntity(flowStepDto.getInvokeScheduler()));
        } else {
            existingFlowStep.setInvokeScheduler(null);
        }
        
        FlowStep updatedFlowStep = flowStepRepository.save(existingFlowStep);
        
        logger.info("Flow step updated successfully with ID: {}", updatedFlowStep.getId());
        return convertToDto(updatedFlowStep);
    }

    public void deleteFlowStep(Long id) {
        logger.info("Deleting flow step with ID: {}", id);
        
        FlowStep flowStep = flowStepRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flow step not found with ID: " + id));
        
        // Delete associated test data
        if (flowStep.getTestDataIds() != null && !flowStep.getTestDataIds().isEmpty()) {
            testDataRepository.deleteByDataIdIn(flowStep.getTestDataIds());
        }
        
        flowStepRepository.deleteById(id);
        logger.info("Flow step deleted successfully with ID: {}", id);
    }

    private List<Long> createTestDataEntries(List<TestDataDto> testDataDtoList) {
        if (testDataDtoList == null || testDataDtoList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return testDataDtoList.stream()
                .map(testDataService::createTestData)
                .map(TestDataDto::getDataId)
                .collect(Collectors.toList());
    }

    private CombinedFlowStepDto convertToDto(FlowStep flowStep) {
        CombinedFlowStepDto dto = new CombinedFlowStepDto();
        dto.setId(flowStep.getId());
        dto.setApplicationId(flowStep.getApplicationId());
        dto.setBranch(flowStep.getBranch());
        dto.setTestTag(flowStep.getTestTag());
        dto.setTestStage(flowStep.getTestStage());
        dto.setDescription(flowStep.getDescription());
        dto.setSquashStepIds(flowStep.getSquashStepIds());
        dto.setCreatedAt(flowStep.getCreatedAt());
        dto.setUpdatedAt(flowStep.getUpdatedAt());
        
        // Get test data
        if (flowStep.getTestDataIds() != null && !flowStep.getTestDataIds().isEmpty()) {
            List<TestDataDto> testDataDtos = flowStep.getTestDataIds().stream()
                    .map(testDataService::getTestDataById)
                    .collect(Collectors.toList());
            dto.setTestData(testDataDtos);
        } else {
            dto.setTestData(new ArrayList<>());
        }
        
        // Handle optional invokeScheduler
        if (flowStep.getInvokeScheduler() != null) {
            dto.setInvokeScheduler(convertInvokeSchedulerEntityToDto(flowStep.getInvokeScheduler()));
        }
        
        return dto;
    }
    
    private com.testautomation.orchestrator.model.InvokeScheduler convertInvokeSchedulerDtoToEntity(com.testautomation.orchestrator.dto.InvokeSchedulerDto dto) {
        if (dto == null) return null;
        
        com.testautomation.orchestrator.model.InvokeScheduler entity = new com.testautomation.orchestrator.model.InvokeScheduler();
        entity.setType(dto.getType());
        
        if (dto.getTimer() != null) {
            com.testautomation.orchestrator.model.Timer timer = new com.testautomation.orchestrator.model.Timer();
            timer.setMinutes(dto.getTimer().getMinutes());
            timer.setHours(dto.getTimer().getHours());
            timer.setDays(dto.getTimer().getDays());
            entity.setTimer(timer);
        }
        
        return entity;
    }
    
    private com.testautomation.orchestrator.dto.InvokeSchedulerDto convertInvokeSchedulerEntityToDto(com.testautomation.orchestrator.model.InvokeScheduler entity) {
        if (entity == null) return null;
        
        com.testautomation.orchestrator.dto.InvokeSchedulerDto dto = new com.testautomation.orchestrator.dto.InvokeSchedulerDto();
        dto.setType(entity.getType());
        
        if (entity.getTimer() != null) {
            com.testautomation.orchestrator.dto.TimerDto timerDto = new com.testautomation.orchestrator.dto.TimerDto();
            timerDto.setMinutes(entity.getTimer().getMinutes());
            timerDto.setHours(entity.getTimer().getHours());
            timerDto.setDays(entity.getTimer().getDays());
            dto.setTimer(timerDto);
        }
        
        return dto;
    }
}
