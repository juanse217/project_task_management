package com.sebastian.dev.projecttaskmanagement.controller.dto;

import java.util.List;

public record PagedResponse<T>(
    int page,
    boolean next, 
    int size,
    List<T> content
) {

}
