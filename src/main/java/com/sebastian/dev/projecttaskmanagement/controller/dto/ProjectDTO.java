package com.sebastian.dev.projecttaskmanagement.controller.dto;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

public record CreateProjectDTO(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        UUID domainId, //provided by the business domain.
        @NotBlank(message = "The NAME is required to create the project")
        @Size(min = 10, max = 100, message = "The project's NAME must be in the 10-100 characters range")
        String name,//Required only on creation
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Set<TaskDTO> tasks //only for them to view the tasks.
) {
}
