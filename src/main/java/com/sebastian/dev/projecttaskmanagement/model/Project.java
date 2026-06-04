package com.sebastian.dev.projecttaskmanagement.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Project {
    private Long id;
    @EqualsAndHashCode.Include
    private final String name;
    private Set<Task> tasks;
}
