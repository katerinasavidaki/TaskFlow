package com.taskflow.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "teams")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"manager", "members"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Team extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "manager_id", nullable = false, unique = true)
    private User manager;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PROTECTED) // Prevent direct modification of the collection
    private Set<User> members = new HashSet<>();

    public void addMember(User user) {
        if (members == null) members = new HashSet<>();
        members.add(user);
        user.setTeam(this);
    }

    public void removeMember(User user) {
        if (members == null) members = new HashSet<>();
        members.remove(user);
        if (user.getTeam() == this) {
            user.setTeam(null); // Clear the team reference in User
        }
    }

    public Set<User> getAllMembers() {
        return Collections.unmodifiableSet(members);
    }
}
