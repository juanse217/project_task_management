package com.sebastian.dev.projecttaskmanagement.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.sebastian.dev.projecttaskmanagement.exception.businessviolation.ProjectAlreadyContainsTaskException;
import com.sebastian.dev.projecttaskmanagement.model.Project;
import com.sebastian.dev.projecttaskmanagement.model.Task;
import com.sebastian.dev.projecttaskmanagement.repository.ProjectDomainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectDomainRepository projectRepository;

    public Slice<Project> findAllProjects(Pageable pageable) {
        return projectRepository.findAllProjects(pageable);
    }

    public Project findProjectById(UUID id) {
        return projectRepository.findProjectByDomainId(id);
    }

    public Project findProjectByName(String name) {
        return projectRepository.findProjectByName(name);
    }

    public Project createProject(Project project) { // object validation with Jakarta
        return projectRepository.save(project);
    }

    public Project updateProject(UUID id, Project project) {
        Project update = findProjectById(id);

        if (project.getName() != null)
            update.updateProjectName(project.getName());

        return projectRepository.save(update);
    }

    public void deleteProject(UUID id) {
        projectRepository.deleteByDomainId(id);
    }

    // End basic CRUD. Task section

    public Task addTask(String projectName, Task task) {
        Project project = findProjectByName(projectName);

        if (!project.addTask(task)) {
            throw new ProjectAlreadyContainsTaskException(
                    String.format("The project %s already contains task %s", projectName, task.getToDo()));
        }
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
        Task task = project.findTask(taskId);

        if (toDo != null)
            task.updateToDo(toDo);
        if (finishDate != null)
            task.updatefinishDate(finishDate);// access memory element, and update

        projectRepository.save(project); // save to DB changes

        return task;
    }

    public Task startTask(String projectName, UUID taskId) {
        Project project = findProjectByName(projectName);
        Task task = project.findTask(taskId);

        task.startTask();

        projectRepository.save(project);

        return task;
    }

    public Task finishTask(String projectName, UUID taskId) {
        Project project = findProjectByName(projectName);
        Task task = project.findTask(taskId);

        task.finishTask();

        projectRepository.save(project);

        return task;
    }

}
