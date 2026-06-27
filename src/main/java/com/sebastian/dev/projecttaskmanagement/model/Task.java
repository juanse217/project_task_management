package com.sebastian.dev.projecttaskmanagement.model;

import java.time.LocalDate;
import java.util.UUID;

import com.sebastian.dev.projecttaskmanagement.exception.businessviolation.TaskNotStartedException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {
    @EqualsAndHashCode.Include
    private UUID id; // Domain id, needed at creation for including in a set.
    private String toDo;
    private Status status;
    private LocalDate finishDate;
    // No need for bidirectional relationship here, only for entities. Project
    // manages tasks /entry point.

    public Task(String toDo, LocalDate finishDate) {
        checkNullity(toDo, finishDate);
        this.id = UUID.randomUUID();
        this.toDo = toDo;
        this.status = Status.NOT_STARTED;
        this.finishDate = finishDate;
    }

    //Not checking nullity since is a DB loaded element
    private Task(UUID id, String toDo, LocalDate finishDate, Status status){
        this.id = id;
        this.toDo = toDo;
        this.finishDate = finishDate;
        this.status = status;
    }

    public static Task reconstituteTask(UUID id, String toDo, LocalDate finishDate, Status status){
        return new Task(id, toDo, finishDate, status);
    }

    public void updateToDo(String toDo) {
        checkNullity(toDo);
        this.toDo = toDo;
    }

    public void updateFinishDate(LocalDate finishDate) {
        checkNullity(finishDate);
        this.finishDate = finishDate;
    }

    public void startTask() {
        this.status = Status.IN_PROGRESS;
    }

    public void finishTask() {
        if (this.getStatus() != Status.IN_PROGRESS) {
            throw new TaskNotStartedException(
                    String.format("The task with id %s cannot be finished because it hasn't been started", this.getId()));
        }
        this.status = Status.FINISHED;
    }

    // Helper
    private void checkNullity(Object... objects) {
        for (Object o : objects) {
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
}
