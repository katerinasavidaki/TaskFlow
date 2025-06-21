package com.taskflow.backend.model;

import com.taskflow.backend.core.enums.TaskPriorityType;
import com.taskflow.backend.core.enums.TaskStatusType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tasks")
@ToString(exclude = {"createdBy", "assignedTo"})
public class Task extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriorityType priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatusType status;

    @Column(name="due_date")
    private LocalDateTime dueDate;

    @Column(name = "is_completed", nullable = false)
    @ColumnDefault("false")
    private Boolean isCompleted;

    // Many-to-One: Many tasks can be created by one user
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    // Many-to-One: Many tasks can be assigned to one user
    @Column(name = "assigned_to_id")
    @ManyToOne
    private User assignedTo;

    /**
     * Sets the creator of this task and maintains bidirectional consistency.
     */
    public void setCreatedBy(User user) {
        if (this.createdBy != null) {
            this.createdBy.getCreatedTasks().remove(this);
        }

        this.createdBy = user;
        if (user != null && !user.getCreatedTasks().contains(this)) {
            user.getCreatedTasks().add(this);
        }
    }

    /**
     * Sets the assignee of this task and maintains bidirectional consistency.
     */
    public void setAssignedTo(User user) {
        if (this.assignedTo != null) {
            this.assignedTo.getAssignedTasks().remove(this);
        }

        this.assignedTo = user;
        if (user != null && !user.getAssignedTasks().contains(this)) {
            user.getAssignedTasks().add(this);
        }

    }
}

