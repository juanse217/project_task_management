package com.sebastian.dev.projecttaskmanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.sebastian.dev.projecttaskmanagement.exception.notfound.ProjectNotFoundException;
import com.sebastian.dev.projecttaskmanagement.model.Project;
import com.sebastian.dev.projecttaskmanagement.repository.entities.ProjectEntity;
import com.sebastian.dev.projecttaskmanagement.repository.mapper.EntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectDomainRepositoryImpl implements ProjectDomainRepository {

    private final ProjectEntityRepository jpaRepository;

    @Override
    public Project findProjectByDomainId(UUID id) {
        ProjectEntity entity = jpaRepository.findByDomainId(id)
                .orElseThrow(() -> new ProjectNotFoundException("The project with id " + id + " not found"));
        return EntityMapper.toProjectModel(entity);

    }

    @Override
    public Project findProjectByName(String name) {
        ProjectEntity entity = jpaRepository.findByName(name)
                .orElseThrow(() -> new ProjectNotFoundException("The project with name " + name + " not found"));
        
        return EntityMapper.toProjectModel(entity);
    }

    @Override
    public Slice<Project> findAllProjects(Pageable pageable) {
        Slice<ProjectEntity> entitySlice = jpaRepository.findAllBy(pageable);
        
        Slice<Project> modelSlice = entitySlice.map(EntityMapper::toProjectModel);

        return modelSlice;

    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = EntityMapper.toProjectEntity(project);
        UUID domainId = project.getId();
        Optional<ProjectEntity> existingEntity = jpaRepository.findByDomainId(domainId);
        //*Hibernate makes INSERTS if the id = null, that's what happens in the code above. 
        //*We check if there's already an entity with the specified UUID, if it does
        //*we retrieve the entity and grab its id to set it into the entity to be saved
        //*This way, we trigger and UPDATE rather than an INSERT.
        if(existingEntity.isPresent()){
            entity.setId(existingEntity.get().getId());
        }
        
        return EntityMapper.toProjectModel(jpaRepository.save(entity));
    }

    @Override
    public void deleteByDomainId(UUID id) {
        jpaRepository.deleteByDomainId(id);
    }

}
