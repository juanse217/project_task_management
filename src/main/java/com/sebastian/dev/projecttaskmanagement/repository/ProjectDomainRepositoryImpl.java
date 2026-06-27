package com.sebastian.dev.projecttaskmanagement.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.sebastian.dev.projecttaskmanagement.repository.entities.TaskEntity;
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

        return entitySlice.map(EntityMapper::toProjectModel);
    }

    @Override
    public Project save(Project project) {
        ProjectEntity saved = null;
        UUID domainId = project.getId();
        Optional<ProjectEntity> existingEntity = jpaRepository.findByDomainId(domainId);
        //*Hibernate makes INSERTS if the id = null, that's what happens in the code above. 
        //*We check if there's already an entity with the specified UUID, if it does.
        //*we retrieve the entity and grab its id to set it into the entity to be saved
        //*This way, we trigger and UPDATE rather than an INSERT.
        //existingEntity.ifPresent(pe -> entity.setId(existingEntity.get().getId()));
        if (existingEntity.isPresent()) {
            Set<TaskEntity> taskEntities = existingEntity.get().getTasks();
            //DELETION
            taskEntities.removeIf(te ->
                    project.findAllTasks().stream().filter(x -> x.getId().equals(te.getDomainId())).findFirst().isEmpty()
            );

            project.findAllTasks().forEach(t -> {
                final Optional<TaskEntity> update = taskEntities.stream().filter(te -> t.getId().equals(te.getDomainId())).findFirst();
                if (update.isPresent()) {
                    update.get().setToDo(t.getToDo());
                    update.get().setFinishDate(t.getFinishDate());
                    update.get().setStatus(t.getStatus()); //In case the status was changed.
                } else {
                    TaskEntity newTask = EntityMapper.toTaskEntity(t);
                    //taskEntities.add(newTask); no need, our addTaskEntity method, adds the task to the set and synchronizes it.
                    existingEntity.get().addTaskEntity(newTask);
                }
            });

            existingEntity.get().setName(project.getName());
            return EntityMapper.toProjectModel(existingEntity.get());
        } else {
            saved = EntityMapper.toProjectEntity(project);
            jpaRepository.save(saved);
        }

        return EntityMapper.toProjectModel(saved);
    }

    @Override
    public void deleteByDomainId(UUID id) {
        jpaRepository.deleteByDomainId(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

}
