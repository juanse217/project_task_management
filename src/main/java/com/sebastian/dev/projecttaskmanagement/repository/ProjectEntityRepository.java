package com.sebastian.dev.projecttaskmanagement.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sebastian.dev.projecttaskmanagement.repository.entities.ProjectEntity;
import java.util.UUID;


/**
 * This interface is used to retrieve the entities from the DB.
 * It'll be injected in our implementation of the domain repository
 */
public interface ProjectEntityRepository extends JpaRepository<ProjectEntity, Long> {
    Optional<ProjectEntity> findByDomainId(UUID id);
    Optional<ProjectEntity> findByName(String name);
    Slice<ProjectEntity> findAllBy(Pageable pageable); //Convention to get all from derived query
    void deleteByDomainId(UUID id);
}
