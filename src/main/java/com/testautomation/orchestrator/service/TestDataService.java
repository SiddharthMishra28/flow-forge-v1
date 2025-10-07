package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.TestDataDto;
import com.testautomation.orchestrator.model.TestData;
import com.testautomation.orchestrator.repository.ApplicationRepository;
import com.testautomation.orchestrator.repository.TestDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestDataService {

    @Autowired
    private TestDataRepository testDataRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public TestDataDto createTestData(TestDataDto testDataDto) {
        if (testDataDto.getApplicationId() == null || !applicationRepository.existsById(testDataDto.getApplicationId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or missing applicationId");
        }
        TestData testData = new TestData();
        testData.setApplicationId(testDataDto.getApplicationId());
        testData.setApplicationName(testDataDto.getApplicationName());
        testData.setCategory(testDataDto.getCategory());
        testData.setDescription(testDataDto.getDescription());
        testData.setVariables(testDataDto.getVariables());
        
        TestData savedTestData = testDataRepository.save(testData);
        return convertToDto(savedTestData);
    }

    public TestDataDto getTestDataById(Long dataId) {
        TestData testData = testDataRepository.findById(dataId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TestData not found with id: " + dataId));
        return convertToDto(testData);
    }

    public List<TestDataDto> getAllTestData() {
        return testDataRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TestDataDto updateTestData(Long dataId, TestDataDto testDataDto) {
        TestData testData = testDataRepository.findById(dataId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TestData not found with id: " + dataId));
        
        if (testDataDto.getApplicationId() != null) {
            if (!applicationRepository.existsById(testDataDto.getApplicationId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid applicationId");
            }
            testData.setApplicationId(testDataDto.getApplicationId());
        }

        if (testDataDto.getApplicationName() != null) {
            testData.setApplicationName(testDataDto.getApplicationName());
        }

        if (testDataDto.getCategory() != null) {
            testData.setCategory(testDataDto.getCategory());
        }

        if (testDataDto.getDescription() != null) {
            testData.setDescription(testDataDto.getDescription());
        }
        
        testData.setVariables(testDataDto.getVariables());
        TestData updatedTestData = testDataRepository.save(testData);
        return convertToDto(updatedTestData);
    }

    public void deleteTestData(Long dataId) {
        if (!testDataRepository.existsById(dataId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TestData not found with id: " + dataId);
        }
        testDataRepository.deleteById(dataId);
    }

    /**
     * Merges multiple test data maps into a single map.
     * Used by FlowExecutionService to merge all test data for a flow step.
     */
    public Map<String, String> mergeTestDataByIds(List<Long> testDataIds) {
        if (testDataIds == null || testDataIds.isEmpty()) {
            return new HashMap<>();
        }

        List<TestData> testDataList = testDataRepository.findByDataIdIn(testDataIds);
        Map<String, String> mergedData = new HashMap<>();
        
        for (TestData testData : testDataList) {
            if (testData.getVariables() != null) {
                mergedData.putAll(testData.getVariables());
            }
        }
        
        return mergedData;
    }

    private TestDataDto convertToDto(TestData testData) {
        TestDataDto dto = new TestDataDto();
        dto.setDataId(testData.getDataId());
        dto.setApplicationId(testData.getApplicationId());
        dto.setApplicationName(testData.getApplicationName());
        dto.setCategory(testData.getCategory());
        dto.setDescription(testData.getDescription());
        dto.setVariables(testData.getVariables());
        dto.setCreatedAt(testData.getCreatedAt());
        dto.setUpdatedAt(testData.getUpdatedAt());
        return dto;
    }
}