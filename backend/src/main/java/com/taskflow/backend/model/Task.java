package com.taskflow.backend.model;

import com.taskflow.backend.core.enums.TaskPriorityType;
import com.taskflow.backend.core.enums.TaskStatusType;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(name = "assigned_to_id")
    @ManyToOne
    private User assignedTo;


}

