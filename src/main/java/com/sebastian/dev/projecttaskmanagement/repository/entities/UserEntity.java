package com.sebastian.dev.projecttaskmanagement.repository.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Table(name = "users") //name in DB table
@Entity(name = "users") //name in app
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)//PW validation at DTO layer
    private String password;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)//So Security has Roles available. This creates 2 separate tables linked by a FK
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id")) //User id is the FK column to map the role to a user. This customized the
    //secondary table created above
    @Column(nullable = false)//targets the secondary table's role column.
    private Set<Role> roles;
}
