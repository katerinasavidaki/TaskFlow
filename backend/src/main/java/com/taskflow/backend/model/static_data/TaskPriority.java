package com.taskflow.backend.model.static_data;

import com.taskflow.backend.core.enums.TaskPriorityType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task_priorities")
public class TaskPriority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *  The type of task priority, e.g., HIGH, MEDIUM, LOW.
     *
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, name = "priority_type")
    private TaskPriorityType priorityType;

    /**
     *  The name of the task priority, e.g., "High", "Medium", "Low".
     *
     */
    @Column(nullable = false)
    private String name;
}
