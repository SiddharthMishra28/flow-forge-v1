package com.testautomation.orchestrator.service;

import com.testautomation.orchestrator.dto.TestDataDto;
import com.testautomation.orchestrator.model.TestData;
import com.testautomation.orchestrator.repository.TestDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestDataService {

    @Autowired
    private TestDataRepository testDataRepository;

    public TestDataDto createTestData(TestDataDto testDataDto) {
        TestData testData = new TestData();
        testData.setTestData(testDataDto.getTestData());
        
        TestData savedTestData = testDataRepository.save(testData);
        return convertToDto(savedTestData);
    }

    public TestDataDto getTestDataById(Long dataId) {
        TestData testData = testDataRepository.findById(dataId)
                .orElseThrow(() -> new RuntimeException("TestData not found with id: " + dataId));
        return convertToDto(testData);
    }

    public List<TestDataDto> getAllTestData() {
        return testDataRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TestDataDto updateTestData(Long dataId, TestDataDto testDataDto) {
        TestData testData = testDataRepository.findById(dataId)
                .orElseThrow(() -> new RuntimeException("TestData not found with id: " + dataId));
        
        testData.setTestData(testDataDto.getTestData());
        TestData updatedTestData = testDataRepository.save(testData);
        return convertToDto(updatedTestData);
    }

    public void deleteTestData(Long dataId) {
        if (!testDataRepository.existsById(dataId)) {
            throw new RuntimeException("TestData not found with id: " + dataId);
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
            if (testData.getTestData() != null) {
                mergedData.putAll(testData.getTestData());
            }
        }
        
        return mergedData;
    }

    private TestDataDto convertToDto(TestData testData) {
        TestDataDto dto = new TestDataDto();
        dto.setDataId(testData.getDataId());
        dto.setTestData(testData.getTestData());
        dto.setCreatedAt(testData.getCreatedAt());
        dto.setUpdatedAt(testData.getUpdatedAt());
        return dto;
    }
}