package com.sebastian.dev.projecttaskmanagement.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {
    @EqualsAndHashCode.Include
    private UUID id; // Domain id, needed at creation for including in a set.
    private String toDo;
    private Status status;
    private LocalDate finishBy;
    // No need for bidirectional relationship here, only for entities. Project
    // manages tasks /entry point.

    public Task(String toDo, LocalDate finishBy) {
        checkNullity(toDo);
        checkNullity(finishBy);
        this.id = UUID.randomUUID();
        this.toDo = toDo;
        this.status = Status.NOT_STARTED;
        this.finishBy = finishBy;
    }

    public void updateToDo(String toDO) {
        checkNullity(toDO);
        this.toDo = toDO;
    }

    public void updateFinishBy(LocalDate finishBy) {
        checkNullity(finishBy);
        this.finishBy = finishBy;
    }

    public void startTask() {
        this.status = Status.IN_PROGRESS;
    }

    public void finishTask() {
        this.status = Status.FINISHED;
    }

    // Helper
    private void checkNullity(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("The object passed cannot be null");
        }
        if (o instanceof String) {
            if (((String) o).isBlank()) {
                throw new IllegalArgumentException("The TODO is not valid");
            }
        }
    }
}
