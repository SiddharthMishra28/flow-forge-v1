package com.testautomation.orchestrator.repository;

import com.testautomation.orchestrator.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    Optional<Application> findByGitlabProjectId(String gitlabProjectId);
    
    boolean existsByGitlabProjectId(String gitlabProjectId);
}