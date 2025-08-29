package com.testautomation.orchestrator.repository;

import com.testautomation.orchestrator.model.Flow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowRepository extends JpaRepository<Flow, Long> {
    
    List<Flow> findBySquashTestCaseId(Long squashTestCaseId);
}