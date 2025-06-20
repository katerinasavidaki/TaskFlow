package com.taskflow.backend.model.static_data;

import com.taskflow.backend.core.enums.RoleType;
import com.taskflow.backend.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The code of the role, based on enum values (e.g. ADMIN, MANAGER, TEAM_LEADER, MEMBER).
     * Used internally in the system for access control.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="role_type")
    private RoleType roleType;

    /**
     * The name of the role, used for display purposes in the UI.
     * For example, "Administrator", "Manager", "Team Leader", "Member".
     */
    @Column(nullable = false)
    private String name;
}
