package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.CombinedFlowDto;
import com.testautomation.orchestrator.dto.CombinedFlowStepDto;
import com.testautomation.orchestrator.dto.DelayDto;
import com.testautomation.orchestrator.dto.InvokeTimerDto;
import com.testautomation.orchestrator.dto.TestDataDto;
import com.testautomation.orchestrator.model.Delay;
import com.testautomation.orchestrator.model.Flow;
import com.testautomation.orchestrator.model.FlowStep;
import com.testautomation.orchestrator.model.InvokeTimer;
import com.testautomation.orchestrator.repository.ApplicationRepository;
import com.testautomation.orchestrator.repository.FlowRepository;
import com.testautomation.orchestrator.repository.FlowStepRepository;
import com.testautomation.orchestrator.repository.TestDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CombinedFlowService {

    private static final Logger logger = LoggerFactory.getLogger(CombinedFlowService.class);

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private FlowStepRepository flowStepRepository;

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private TestDataService testDataService;

    public CombinedFlowDto createCombinedFlow(CombinedFlowDto combinedFlowDto) {
        logger.info("Creating new combined flow with {} steps", combinedFlowDto.getFlowSteps().size());
        
        // Validate all applications exist
        validateApplicationsExist(combinedFlowDto.getFlowSteps());
        
        // Create and save flow steps with test data
        List<Long> flowStepIds = new ArrayList<>();
        List<FlowStep> savedFlowSteps = new ArrayList<>();
        
        for (CombinedFlowStepDto stepDto : combinedFlowDto.getFlowSteps()) {
            // Create test data entries first
            List<Long> testDataIds = createTestDataEntries(stepDto.getTestData());
            
            // Create flow step
            FlowStep flowStep = new FlowStep();
            flowStep.setApplicationId(stepDto.getApplicationId());
            flowStep.setBranch(stepDto.getBranch());
            flowStep.setTestTag(stepDto.getTestTag());
            flowStep.setTestStage(stepDto.getTestStage());
            flowStep.setDescription(stepDto.getDescription());
            flowStep.setSquashStepIds(stepDto.getSquashStepIds());
            flowStep.setTestDataIds(testDataIds);
            flowStep.setInvokeTimer(convertInvokeTimerDtoToEntity(stepDto.getInvokeTimer()));
            
            FlowStep savedFlowStep = flowStepRepository.save(flowStep);
            savedFlowSteps.add(savedFlowStep);
            flowStepIds.add(savedFlowStep.getId());
        }
        
        // Create flow
        Flow flow = new Flow();
        flow.setFlowStepIds(flowStepIds);
        flow.setSquashTestCaseId(combinedFlowDto.getSquashTestCaseId());
        
        Flow savedFlow = flowRepository.save(flow);
        
        logger.info("Combined flow created with ID: {}", savedFlow.getId());
        return convertToDto(savedFlow, savedFlowSteps);
    }

    @Transactional(readOnly = true)
    public Optional<CombinedFlowDto> getCombinedFlowById(Long id) {
        logger.debug("Fetching combined flow with ID: {}", id);
        
        Optional<Flow> flowOpt = flowRepository.findById(id);
        if (flowOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Flow flow = flowOpt.get();
        List<FlowStep> flowSteps = flowStepRepository.findByIdIn(flow.getFlowStepIds());
        
        return Optional.of(convertToDto(flow, flowSteps));
    }

    @Transactional(readOnly = true)
    public List<CombinedFlowDto> getAllCombinedFlows() {
        logger.debug("Fetching all combined flows");
        
        List<Flow> flows = flowRepository.findAll();
        return flows.stream()
                .map(flow -> {
                    List<FlowStep> flowSteps = flowStepRepository.findByIdIn(flow.getFlowStepIds());
                    return convertToDto(flow, flowSteps);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CombinedFlowDto> getCombinedFlowsBySquashTestCaseId(Long squashTestCaseId) {
        logger.debug("Fetching combined flows for Squash test case ID: {}", squashTestCaseId);
        
        List<Flow> flows = flowRepository.findBySquashTestCaseId(squashTestCaseId);
        return flows.stream()
                .map(flow -> {
                    List<FlowStep> flowSteps = flowStepRepository.findByIdIn(flow.getFlowStepIds());
                    return convertToDto(flow, flowSteps);
                })
                .collect(Collectors.toList());
    }

    public CombinedFlowDto updateCombinedFlow(Long id, CombinedFlowDto combinedFlowDto) {
        logger.info("Updating combined flow with ID: {}", id);
        
        Flow existingFlow = flowRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flow not found with ID: " + id));
        
        // Validate all applications exist
        validateApplicationsExist(combinedFlowDto.getFlowSteps());
        
        // Delete old flow steps and their test data
        List<FlowStep> oldFlowSteps = flowStepRepository.findByIdIn(existingFlow.getFlowStepIds());
        deleteFlowStepsAndTestData(oldFlowSteps);
        
        // Create new flow steps with test data
        List<Long> newFlowStepIds = new ArrayList<>();
        List<FlowStep> newFlowSteps = new ArrayList<>();
        
        for (CombinedFlowStepDto stepDto : combinedFlowDto.getFlowSteps()) {
            // Create test data entries first
            List<Long> testDataIds = createTestDataEntries(stepDto.getTestData());
            
            // Create flow step
            FlowStep flowStep = new FlowStep();
            flowStep.setApplicationId(stepDto.getApplicationId());
            flowStep.setBranch(stepDto.getBranch());
            flowStep.setTestTag(stepDto.getTestTag());
            flowStep.setTestStage(stepDto.getTestStage());
            flowStep.setDescription(stepDto.getDescription());
            flowStep.setSquashStepIds(stepDto.getSquashStepIds());
            flowStep.setTestDataIds(testDataIds);
            flowStep.setInvokeTimer(convertInvokeTimerDtoToEntity(stepDto.getInvokeTimer()));
            
            FlowStep savedFlowStep = flowStepRepository.save(flowStep);
            newFlowSteps.add(savedFlowStep);
            newFlowStepIds.add(savedFlowStep.getId());
        }
        
        // Update flow
        existingFlow.setFlowStepIds(newFlowStepIds);
        existingFlow.setSquashTestCaseId(combinedFlowDto.getSquashTestCaseId());
        
        Flow updatedFlow = flowRepository.save(existingFlow);
        
        logger.info("Combined flow updated successfully with ID: {}", updatedFlow.getId());
        return convertToDto(updatedFlow, newFlowSteps);
    }

    public void deleteCombinedFlow(Long id) {
        logger.info("Deleting combined flow with ID: {}", id);
        
        Flow flow = flowRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Flow not found with ID: " + id));
        
        // Delete flow steps and their test data
        List<FlowStep> flowSteps = flowStepRepository.findByIdIn(flow.getFlowStepIds());
        deleteFlowStepsAndTestData(flowSteps);
        
        // Delete flow
        flowRepository.deleteById(id);
        logger.info("Combined flow deleted successfully with ID: {}", id);
    }

    private void validateApplicationsExist(List<CombinedFlowStepDto> flowSteps) {
        List<Long> applicationIds = flowSteps.stream()
                .map(CombinedFlowStepDto::getApplicationId)
                .distinct()
                .collect(Collectors.toList());
        
        for (Long applicationId : applicationIds) {
            if (!applicationRepository.existsById(applicationId)) {
                throw new IllegalArgumentException("Application not found with ID: " + applicationId);
            }
        }
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

    private void deleteFlowStepsAndTestData(List<FlowStep> flowSteps) {
        for (FlowStep flowStep : flowSteps) {
            // Delete associated test data
            if (flowStep.getTestDataIds() != null && !flowStep.getTestDataIds().isEmpty()) {
                testDataRepository.deleteByDataIdIn(flowStep.getTestDataIds());
            }
            // Delete flow step
            flowStepRepository.delete(flowStep);
        }
    }

    private CombinedFlowDto convertToDto(Flow flow, List<FlowStep> flowSteps) {
        CombinedFlowDto dto = new CombinedFlowDto();
        dto.setId(flow.getId());
        dto.setSquashTestCaseId(flow.getSquashTestCaseId());
        dto.setCreatedAt(flow.getCreatedAt());
        dto.setUpdatedAt(flow.getUpdatedAt());
        
        // Convert flow steps
        List<CombinedFlowStepDto> flowStepDtos = flowSteps.stream()
                .map(this::convertFlowStepToDto)
                .collect(Collectors.toList());
        
        dto.setFlowSteps(flowStepDtos);
        return dto;
    }

    private CombinedFlowStepDto convertFlowStepToDto(FlowStep flowStep) {
        CombinedFlowStepDto dto = new CombinedFlowStepDto();
        dto.setId(flowStep.getId());
        dto.setApplicationId(flowStep.getApplicationId());
        dto.setBranch(flowStep.getBranch());
        dto.setTestTag(flowStep.getTestTag());
        dto.setTestStage(flowStep.getTestStage());
        dto.setDescription(flowStep.getDescription());
        dto.setSquashStepIds(flowStep.getSquashStepIds());
        dto.setInvokeTimer(convertInvokeTimerEntityToDto(flowStep.getInvokeTimer()));
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
        
        return dto;
    }

    private InvokeTimer convertInvokeTimerDtoToEntity(InvokeTimerDto dto) {
        if (dto == null) {
            return null;
        }
        
        InvokeTimer entity = new InvokeTimer();
        entity.setIsScheduled(dto.getIsScheduled());
        entity.setScheduledCron(dto.getScheduledCron());
        
        if (dto.getDelay() != null) {
            Delay delay = new Delay();
            delay.setTimeUnit(dto.getDelay().getTimeUnit());
            delay.setValue(dto.getDelay().getValue());
            entity.setDelay(delay);
        }
        
        return entity;
    }

    private InvokeTimerDto convertInvokeTimerEntityToDto(InvokeTimer entity) {
        if (entity == null) {
            return null;
        }
        
        InvokeTimerDto dto = new InvokeTimerDto();
        dto.setIsScheduled(entity.getIsScheduled());
        dto.setScheduledCron(entity.getScheduledCron());
        
        if (entity.getDelay() != null) {
            DelayDto delayDto = new DelayDto();
            delayDto.setTimeUnit(entity.getDelay().getTimeUnit());
            delayDto.setValue(entity.getDelay().getValue());
            dto.setDelay(delayDto);
        }
        
        return dto;
    }
}
