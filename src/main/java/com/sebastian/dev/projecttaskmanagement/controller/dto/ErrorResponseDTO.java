package com.sebastian.dev.projecttaskmanagement.controller.dto;

public record ErrorResponseDTO(
        String field,
        String message,
        String code
) {
}
