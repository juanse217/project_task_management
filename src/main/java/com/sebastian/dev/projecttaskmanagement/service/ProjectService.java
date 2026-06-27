package com.sebastian.dev.projecttaskmanagement.service;

import java.time.LocalDate;
import java.util.UUID;

import com.sebastian.dev.projecttaskmanagement.exception.businessviolation.NameAlreadyInUseException;
import com.sebastian.dev.projecttaskmanagement.exception.businessviolation.ProjectAlreadyExistsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.sebastian.dev.projecttaskmanagement.exception.businessviolation.ProjectAlreadyContainsTaskException;
import com.sebastian.dev.projecttaskmanagement.model.Project;
import com.sebastian.dev.projecttaskmanagement.model.Task;
import com.sebastian.dev.projecttaskmanagement.repository.ProjectDomainRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectDomainRepository projectRepository;

    @Transactional(readOnly = true)
    public Slice<Project> findAllProjects(Pageable pageable) {
        return projectRepository.findAllProjects(pageable);
    }

    @Transactional(readOnly = true)
    public Project findProjectById(UUID id) {
        return projectRepository.findProjectByDomainId(id);
    }

    @Transactional(readOnly = true)
    public Project findProjectByName(String name) {
        return projectRepository.findProjectByName(name);
    }

    public Project createProject(Project project) { // object validation with Jakarta
        if (projectRepository.existsByName(project.getName())) {
            throw new ProjectAlreadyExistsException("The project with name " + project.getName() + " already exists");
        }
        return projectRepository.save(project);
    }

    public Project updateProject(UUID id, String projectName) {
        Project update = findProjectById(id);

        if (projectName != null) {
            if (projectRepository.existsByName(projectName)) {
                throw new NameAlreadyInUseException("The name " + projectName + " is already in use by another project");
            }

            update.updateProjectName(projectName);
        }


        return projectRepository.save(update);
    }

    public void deleteProject(UUID id) {
        projectRepository.deleteByDomainId(id);
    }

    // End basic CRUD. Task section

    public Task addTask(String projectName, Task task) {
        Project project = findProjectByName(projectName);
        project.addTask(task);
        projectRepository.save(project); // saves the new tasks added
        return task;
    }

    public void deleteTask(String projectName, UUID taskId) {
        Project project = findProjectByName(projectName);
        Task task = project.findTask(taskId);
        project.deleteTask(task);
        projectRepository.save(project); // save to DB the changes.
    }

    public Task updateTask(String projectName, UUID taskId, String toDo, LocalDate finishDate) {
        Project project = findProjectByName(projectName);
        Task task = project.updateTask(taskId, toDo, finishDate);
        projectRepository.save(project); // save to DB changes
        return task;
    }

    public Task startTask(String projectName, UUID taskId) {
        Project project = findProjectByName(projectName);
        Task task = project.startTask(taskId);

        projectRepository.save(project);

        return task;
    }

    public Task finishTask(String projectName, UUID taskId) {
        Project project = findProjectByName(projectName);
        Task task = project.finishTask(taskId);

        projectRepository.save(project);

        return task;
    }

}
