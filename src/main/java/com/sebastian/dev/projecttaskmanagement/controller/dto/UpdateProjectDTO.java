package com.sebastian.dev.projecttaskmanagement.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectDTO(
        @NotBlank(message = "The NAME is required to update the project")
        @Size(min = 10, max = 100, message = "The project's NAME must be in the 10-100 characters range")
        String name
) {
}
