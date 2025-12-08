package com.testautomation.orchestrator.repository;

import com.testautomation.orchestrator.model.TestData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDataRepository extends JpaRepository<TestData, Long> {
    
    List<TestData> findByDataIdIn(List<Long> dataIds);
    
    List<TestData> findByApplicationId(Long applicationId);
    
    Page<TestData> findByApplicationId(Long applicationId, Pageable pageable);
    
    void deleteByDataIdIn(List<Long> dataIds);
    
    boolean existsByDataId(Long dataId);
}