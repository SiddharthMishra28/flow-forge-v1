package com.testautomation.orchestrator.controller;

import com.testautomation.orchestrator.dto.TestDataDto;
import com.testautomation.orchestrator.service.TestDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-data")
@Tag(name = "Test Data", description = "Test Data management operations")
public class TestDataController {

    @Autowired
    private TestDataService testDataService;

    @PostMapping
    @Operation(summary = "Create new test data", description = "Creates a new test data entry with key-value pairs")
    public ResponseEntity<TestDataDto> createTestData(@Valid @RequestBody TestDataDto testDataDto) {
        TestDataDto createdTestData = testDataService.createTestData(testDataDto);
        return new ResponseEntity<>(createdTestData, HttpStatus.CREATED);
    }

    @GetMapping("/{dataId}")
    @Operation(summary = "Get test data by ID", description = "Retrieves test data by its unique identifier")
    public ResponseEntity<TestDataDto> getTestDataById(@PathVariable Long dataId) {
        TestDataDto testData = testDataService.getTestDataById(dataId);
        return ResponseEntity.ok(testData);
    }

    @GetMapping
    @Operation(summary = "Get all test data", description = "Retrieves all test data entries")
    public ResponseEntity<List<TestDataDto>> getAllTestData() {
        List<TestDataDto> testDataList = testDataService.getAllTestData();
        return ResponseEntity.ok(testDataList);
    }

    @PutMapping("/{dataId}")
    @Operation(summary = "Update test data", description = "Updates an existing test data entry")
    public ResponseEntity<TestDataDto> updateTestData(@PathVariable Long dataId, 
                                                     @Valid @RequestBody TestDataDto testDataDto) {
        TestDataDto updatedTestData = testDataService.updateTestData(dataId, testDataDto);
        return ResponseEntity.ok(updatedTestData);
    }

    @DeleteMapping("/{dataId}")
    @Operation(summary = "Delete test data", description = "Deletes a test data entry by its ID")
    public ResponseEntity<Void> deleteTestData(@PathVariable Long dataId) {
        testDataService.deleteTestData(dataId);
        return ResponseEntity.noContent().build();
    }
}