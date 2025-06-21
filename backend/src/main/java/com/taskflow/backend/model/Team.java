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
public class Team extends AbstractEntity {

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToOne(optional = false)
    @Column(name = "team_leader_id", unique = true, nullable = false)
    @JoinColumn(name = "team_leader_id", nullable = false, unique = true)
    @Setter(AccessLevel.PROTECTED) // Prevent direct modification of the team leader
    private User teamLeader;

    @ManyToOne(optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    @Setter(AccessLevel.PROTECTED) // Prevent direct modification of the manager
    private User manager;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.PROTECTED) // Prevent direct modification of the collection
    private List<User> members = new ArrayList<>();

    public void addMember(User user) {
        if (members == null) members = new ArrayList<>();
        members.add(user);
        if (user.getTeam() != this) {
            user.setTeam(this);
        }
    }

    public void removeMember(User user) {
        if (members == null) members = new ArrayList<>();
        members.remove(user);
        if (user.getTeam() == this) {
            user.setTeam(null);
        }
    }

    public List<User> getAllMembers() {
        return Collections.unmodifiableList(members);
    }
}
