package com.sebastian.dev.projecttaskmanagement.controller;

import com.sebastian.dev.projecttaskmanagement.controller.dto.TaskDTO;
import com.sebastian.dev.projecttaskmanagement.model.Task;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sebastian.dev.projecttaskmanagement.controller.dto.PagedResponse;
import com.sebastian.dev.projecttaskmanagement.controller.dto.ProjectDTO;
import com.sebastian.dev.projecttaskmanagement.controller.mapper.DTOMapper;
import com.sebastian.dev.projecttaskmanagement.model.Project;
import com.sebastian.dev.projecttaskmanagement.service.ProjectService;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<PagedResponse<ProjectDTO>> findAllProjects(Pageable pageable) {
        Slice<ProjectDTO> slice = projectService.findAllProjects(pageable).map(DTOMapper::toProjectDTO);
        return ResponseEntity.ok(new PagedResponse<>(slice.getNumber(), slice.hasNext(), slice.getSize(), slice.getContent()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> findProjectById(@PathVariable UUID id) {
        ProjectDTO responseDTO = DTOMapper.toProjectDTO(projectService.findProjectById(id));
        return ResponseEntity.ok(responseDTO);
    }
    @GetMapping("name")
    public ResponseEntity<ProjectDTO> findProjectByName(@RequestParam String name){
        ProjectDTO responseDTO = DTOMapper.toProjectDTO(projectService.findProjectByName(name));
        return ResponseEntity.ok(responseDTO);
    }
    
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Validated(ProjectDTO.OnProjectCreate.class)@RequestBody ProjectDTO dto) {
        Project project = DTOMapper.toProject(dto);
        ProjectDTO responseDTO = DTOMapper.toProjectDTO(projectService.createProject(project));
        return ResponseEntity.created(URI.create(ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(responseDTO.domainId()).toUriString())).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable UUID id, @Valid @RequestBody ProjectDTO dto){
        Project project = DTOMapper.toProject(dto);
        ProjectDTO responseDTO = DTOMapper.toProjectDTO(projectService.updateProject(id, project));

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    //Task management

    @PostMapping("/{projectName}/tasks")
    public ResponseEntity<TaskDTO> addTask(@Validated(TaskDTO.OnTaskCreate.class) @RequestBody TaskDTO dto, @PathVariable String projectName){
        Task task = DTOMapper.toTask(dto);
        TaskDTO responseDTO = DTOMapper.toTaskDTO(projectService.addTask(projectName, task));
        return ResponseEntity.created(URI.create(ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(responseDTO.id()).toUriString())).body(responseDTO);
    }

    @PutMapping("/{projectName}/tasks/{id}")
    public ResponseEntity<TaskDTO> updateTask(@Valid @RequestBody TaskDTO dto, @PathVariable UUID id, @PathVariable String projectName){
        TaskDTO responseDTO = DTOMapper.toTaskDTO(projectService.updateTask(projectName, id, dto.toDo(), dto.finishDate()));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{projectName}/tasks/{id}/activations")
    public ResponseEntity<TaskDTO> startTask(@PathVariable String projectName, @PathVariable UUID id){
        TaskDTO responseDTO = DTOMapper.toTaskDTO(projectService.startTask(projectName, id));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{projectName}/tasks/{id}/completions")
    public ResponseEntity<TaskDTO> finishTask(@PathVariable String projectName, @PathVariable UUID id){
        TaskDTO responseDTO = DTOMapper.toTaskDTO(projectService.finishTask(projectName, id));
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{projectName}/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String projectName, @PathVariable UUID id){
        projectService.deleteTask(projectName, id);
        return ResponseEntity.noContent().build();
    }

}
