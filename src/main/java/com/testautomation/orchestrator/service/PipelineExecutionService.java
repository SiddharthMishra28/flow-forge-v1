package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.PipelineExecutionDto;
import com.testautomation.orchestrator.model.PipelineExecution;
import com.testautomation.orchestrator.repository.PipelineExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PipelineExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(PipelineExecutionService.class);

    @Autowired
    private PipelineExecutionRepository pipelineExecutionRepository;

    public List<PipelineExecutionDto> getPipelineExecutionsByFlowExecutionId(UUID flowExecutionId) {
        logger.debug("Fetching pipeline executions for flow execution ID: {}", flowExecutionId);
        return pipelineExecutionRepository.findByFlowExecutionIdOrderByCreatedAt(flowExecutionId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PipelineExecutionDto> getPipelineExecutionById(Long pipelineExecutionId) {
        logger.debug("Fetching pipeline execution with ID: {}", pipelineExecutionId);
        return pipelineExecutionRepository.findById(pipelineExecutionId)
                .map(this::convertToDto);
    }

    public List<PipelineExecutionDto> getPipelineExecutionsByFlowId(Long flowId) {
        logger.debug("Fetching pipeline executions for flow ID: {}", flowId);
        return pipelineExecutionRepository.findByFlowId(flowId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<PipelineExecutionDto> getPipelineExecutionsByFlowStepId(Long flowStepId) {
        logger.debug("Fetching pipeline executions for flow step ID: {}", flowStepId);
        return pipelineExecutionRepository.findByFlowStepId(flowStepId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PipelineExecutionDto convertToDto(PipelineExecution entity) {
        PipelineExecutionDto dto = new PipelineExecutionDto();
        dto.setId(entity.getId());
        dto.setFlowId(entity.getFlowId());
        dto.setFlowExecutionId(entity.getFlowExecutionId());
        dto.setFlowStepId(entity.getFlowStepId());
        dto.setPipelineId(entity.getPipelineId());
        dto.setPipelineUrl(entity.getPipelineUrl());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setInitialTestData(entity.getInitialTestData());
        dto.setRuntimeTestData(entity.getRuntimeTestData());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}