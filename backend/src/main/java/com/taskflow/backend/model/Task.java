package com.taskflow.backend.model;

import com.taskflow.backend.core.enums.TaskPriorityType;
import com.taskflow.backend.core.enums.TaskStatusType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tasks")
@ToString(exclude = {"createdBy", "assignedTo"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Task extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriorityType priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatusType status;

    @Column(name="due_date")
    private LocalDate dueDate;

    @Column(name = "is_completed", nullable = false)
    @ColumnDefault("false")
    private Boolean isCompleted;

    // Many-to-One: Many tasks can be created by one user
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    // Many-to-One: Many tasks can be assigned to one user
    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}

