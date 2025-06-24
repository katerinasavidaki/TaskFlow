package com.taskflow.backend.model;

import com.taskflow.backend.core.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


/**
 * Represents an application user in the system with authentication details and role.
 * Implements Spring Security {@link UserDetails} for integration with authentication.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@ToString(exclude = {"password", "assignedTasks", "createdTasks"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(unique = true, nullable = false)
    private String username;        // email as login identifier

    @Column(nullable = false)
    private String password;

    @NotNull(message = "AFM is required")
    private String afm;

    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "createdBy")
    @Setter(AccessLevel.PROTECTED) // Prevents direct setting,
    private Set<Task> createdTasks = new HashSet<>();

    // One-to-Many: A user can be assigned many tasks
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PROTECTED) // Prevents direct setting,
    // use addAssignedTask and removeAssignedTask methods instead
    private Set<Task> assignedTasks = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /**
     * Initializes the UUID before persisting the entity if it is not already set.
     * This method is called automatically by JPA before the entity is persisted.
     */
    @PrePersist
    public void initializeUUID() {
        if (uuid == null) uuid = UUID.randomUUID().toString();
    }

    public Set<Task> getAllAssignedTasks() {
        return Collections.unmodifiableSet(assignedTasks);
    }

    public Set<Task> getAllCreatedTasks() {
        return Collections.unmodifiableSet(createdTasks);
    }

    public void addCreatedTask(Task task) {
        if (createdTasks == null) createdTasks = new HashSet<>();
        createdTasks.add(task);
        task.setCreatedBy(this);
    }

    public void removeCreatedTask(Task task) {
        if (createdTasks == null) createdTasks = new HashSet<>();
        createdTasks.remove(task);
        if (task.getCreatedBy() == this) {
            task.setCreatedBy(null);
        }
    }

    public void addAssignedTask(Task task) {
        if (assignedTasks == null) assignedTasks = new HashSet<>();
        assignedTasks.add(task);
        task.setAssignedTo(this);
    }

    public void removeAssignedTask(Task task) {
        if (assignedTasks == null) assignedTasks = new HashSet<>();
        assignedTasks.remove(task);
        if (task.getAssignedTo() == this) {
            task.setAssignedTo(null);
        }
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("Role " + role.name()));
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return this.isActive;
//    }
}
