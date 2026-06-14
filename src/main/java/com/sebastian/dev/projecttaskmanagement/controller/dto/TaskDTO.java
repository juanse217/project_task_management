package com.sebastian.dev.projecttaskmanagement.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sebastian.dev.projecttaskmanagement.model.Status;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

public record TaskDTO(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    UUID id,
    @NotBlank(groups = OnTaskCreate.class, message = "The TODO is required for Task creation")
    @Size(min = 5, message = "The TODO must be at least 5 characters")
    String toDo,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Status status,
    @NotNull(groups = OnTaskCreate.class, message = "The DATE is required for Task creation")
    @FutureOrPresent(message = "The DATE must be in the future or present")
    LocalDate finishDate
) {
    public interface OnTaskCreate extends Default{}
}
