package com.sebastian.dev.projecttaskmanagement.model;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

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

    public void addTask(Task task) {
        checkNullity(task);
        tasks.add(task);
    }

    public void deleteTask(Task task) {
        checkNullity(task);
        tasks.remove(task);
    }

    public void updateProjectName(String name) { // can update tasks by deleting and adding.
        checkNullity(name);
        if (name.equals(this.name)) {
            throw new IllegalArgumentException("The name " + name + " has already been assigned");
        }
        this.name = name;
    }

    public Set<Task> findTasks() {
        return Collections.unmodifiableSet(tasks); // safe copying.
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
