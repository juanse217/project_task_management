package com.sebastian.dev.projecttaskmanagement.repository.entities;

import java.util.UUID;

import com.sebastian.dev.projecttaskmanagement.model.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tasks")
@Table(name = "tasks")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "domain_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID domainId;
    @Column(name = "to_do", nullable = false)
    private String toDo;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // save as String.
    private Status status;
    @ManyToOne(fetch = FetchType.LAZY) // Child entity (owning side) always with JoinColumn
    @JoinColumn(name = "project_id")
    private ProjectEntity project;
}
