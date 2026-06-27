package com.sebastian.dev.projecttaskmanagement.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.sebastian.dev.projecttaskmanagement.model.Project;

/**
 * This class is our domain repository. It deals directly with 
 * our Project domain model
 */
public interface ProjectDomainRepository {
    Project findProjectByDomainId(UUID id);
    Project findProjectByName(String name);
    Slice<Project> findAllProjects(Pageable pageable);
    Project save(Project project);
    void deleteByDomainId(UUID id);
    boolean existsByName(String name);
}
