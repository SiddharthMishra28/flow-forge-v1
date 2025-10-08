package com.testautomation.orchestrator.repository;

import com.testautomation.orchestrator.model.FlowStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowStepRepository extends JpaRepository<FlowStep, Long> {
    
    List<FlowStep> findByApplicationId(Long applicationId);
    
    Page<FlowStep> findByApplicationId(Long applicationId, Pageable pageable);
    
    List<FlowStep> findByIdIn(List<Long> ids);
}