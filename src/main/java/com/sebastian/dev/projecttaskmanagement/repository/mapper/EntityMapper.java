package com.sebastian.dev.projecttaskmanagement.repository.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.sebastian.dev.projecttaskmanagement.model.Project;
import com.sebastian.dev.projecttaskmanagement.model.Task;
import com.sebastian.dev.projecttaskmanagement.repository.entities.ProjectEntity;
import com.sebastian.dev.projecttaskmanagement.repository.entities.TaskEntity;

public class EntityMapper {

    public static Project toProjectModel(ProjectEntity entity) {
        Set<Task> models = entity.getTasks()
                .stream()
                .map(te -> toTaskModel(te))
                .collect(Collectors.toSet());

        Project p = Project.reconstituteProject(entity.getDomainId(), entity.getName(), models);

        return p;
    }

    public static Task toTaskModel(TaskEntity entity) {
        //*This way, we avoid creating a random UUID (using the constructor)
        //*and having setters that can mutate an object
        Task t = Task.reconstituteTask(entity.getDomainId(), entity.getToDo(), entity.getFinishDate(),
                entity.getStatus());
        return t;
    }

    public static ProjectEntity toProjectEntity(Project model) {
        ProjectEntity pe = new ProjectEntity();
        pe.setDomainId(model.getId());
        pe.setName(model.getName());

        for (Task t : model.findAllTasks()) {
            pe.addTaskEntity(toTaskEntity(t));//*syncs both entities.
        }

        return pe;
    }

    public static TaskEntity toTaskEntity(Task model) {
        TaskEntity te = new TaskEntity();
        te.setDomainId(model.getId());
        te.setFinishDate(model.getFinishDate());
        te.setStatus(model.getStatus());
        te.setToDo(model.getToDo());

        return te;
    }
}
