package com.sebastian.dev.projecttaskmanagement.controller.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.sebastian.dev.projecttaskmanagement.controller.dto.ProjectDTO;
import com.sebastian.dev.projecttaskmanagement.controller.dto.TaskDTO;
import com.sebastian.dev.projecttaskmanagement.model.Project;
import com.sebastian.dev.projecttaskmanagement.model.Task;

public class DTOMapper {
    public static ProjectDTO toProjectDTO(Project p){
        Set<TaskDTO> tasks = p.findAllTasks()
                            .stream()
                            .map(t -> toTaskDTO(t))
                            .collect(Collectors.toUnmodifiableSet());
        ProjectDTO dto = new ProjectDTO(p.getId(), p.getName(), tasks);
        return dto;
    }

    public static TaskDTO toTaskDTO(Task t){
        TaskDTO dto = new TaskDTO(t.getId(), t.getToDo(), t.getStatus(), t.getFinishDate());

        return dto;
    }

    public static Project toProject(ProjectDTO dto){
        Project p = new Project(dto.name());
        return p; 
    }
    public static Task toTask(TaskDTO dto){
        Task t = new Task(dto.toDo(), dto.finishDate());
        return t;
    }
}
