package com.taskflow.backend.model;

import com.taskflow.backend.model.static_data.Role;
import jakarta.persistence.*;
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
@ToString(exclude = {"password", "userInfo", "createdTasks", "assignedTasks"})
public class User extends AbstractEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE) // Prevents direct setting, use setUserInfo method instead
    private UserInfo userInfo;

    @OneToMany(mappedBy = "createdBy")
    private List<Task> createdTasks = new ArrayList<>();

    @OneToMany(mappedBy = "assignedTo")
    private List<Task> assignedTasks = new ArrayList<>();

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        if (userInfo != null && userInfo.getUser() != this) {
            userInfo.setUser(this);
        }
    }

    public void removeUserInfo() {
        if (this.userInfo != null) {
            this.userInfo.setUser(null);
            this.userInfo = null;
        }
    }

    /**
     * Initializes the UUID before persisting the entity if it is not already set.
     * This method is called automatically by JPA before the entity is persisted.
     */
    @PrePersist
    public void initializeUUID() {
        if (uuid == null) uuid = UUID.randomUUID().toString();
    }

    public List<Task> getAllCreatedTasks() {
        return Collections.unmodifiableList(createdTasks);
    }

    public List<Task> getAllAssignedTasks() {
        return Collections.unmodifiableList(assignedTasks);
    }

    public void addCreatedTask(Task task) {
        if (createdTasks == null) createdTasks = new ArrayList<>();
        createdTasks.add(task);
        task.setCreatedBy(this);
    }

    public void removeCreatedTask(Task task) {
        if (createdTasks == null) createdTasks = new ArrayList<>();
        createdTasks.remove(task);
        task.setCreatedBy(null);
    }

    public void addAssignedTask(Task task) {
        if (assignedTasks == null) assignedTasks = new ArrayList<>();
        assignedTasks.add(task);
        task.setAssignedTo(this);
    }

    public void removeAssignedTask(Task task) {
        if (assignedTasks == null) assignedTasks = new ArrayList<>();
        assignedTasks.remove(task);
        task.setAssignedTo(null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("Role " + role.getRoleType().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }
}
