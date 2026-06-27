package com.sebastian.dev.projecttaskmanagement.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

import com.sebastian.dev.projecttaskmanagement.exception.businessviolation.NameAlreadyInUseException;
import com.sebastian.dev.projecttaskmanagement.exception.businessviolation.ProjectAlreadyContainsTaskException;
import com.sebastian.dev.projecttaskmanagement.exception.notfound.TaskNotFoundException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Project {
    @Getter
    @EqualsAndHashCode.Include
    private UUID id;
    @Getter
    private String name; // uniqueness enforced in DB level.

    private final Set<Task> tasks = new HashSet<>();

    public Project(String name) {
        checkNullity(name);
        this.id = UUID.randomUUID();
        this.name = name;
    }

    private Project(UUID id, String name, Set<Task> tasks) {
        this.id = id;
        this.name = name;
        for (Task t : tasks) {
            addTask(t);
        }
    }

    public static Project reconstituteProject(UUID id, String name, Set<Task> tasks) {
        return new Project(id, name, tasks);
    }

    public void updateProjectName(String name) {
        checkNullity(name);
        this.name = name;
    }

    public void addTask(Task task) {
        checkNullity(task);
        boolean added = this.tasks.add(task);//true if the set didn't contain the task
        if (!added) {
            throw new ProjectAlreadyContainsTaskException(
                    String.format("The project %s already contains task %s", this.name, task.getToDo()));
        }
    }

    public Task updateTask(UUID taskId, String toDo, LocalDate finishDate){
        Task task = findTask(taskId);
        if (toDo != null)
            task.updateToDo(toDo);
        if (finishDate != null)
            task.updateFinishDate(finishDate);// access memory element, and update

        return task;
    }

    public Task startTask(UUID taskId){
        Task task = findTask(taskId);
        task.startTask();
        return task;
    }

    public Task finishTask(UUID taskId){
        Task task = findTask(taskId);
        task.finishTask();
        return task;

    }

    public void deleteTask(Task task) {
        checkNullity(task);
        tasks.remove(task);
    }

    public Set<Task> findAllTasks() {
        return Collections.unmodifiableSet(tasks); // safe copying.
    }

    public Task findTask(UUID taskId) {
        return this.tasks
                .stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException(String.format("The task with id %s not found", taskId)));
    }


    private void checkNullity(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("The object passed cannot be null");
        }
        if (o instanceof String) {
            if (((String) o).isBlank()) {
                throw new IllegalArgumentException("The name is not valid");
            }
        }
    }
}
