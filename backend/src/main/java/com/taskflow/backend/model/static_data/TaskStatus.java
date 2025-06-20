package com.taskflow.backend.model.static_data;

import com.taskflow.backend.core.enums.TaskStatusType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task_statuses")
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, name = "status_type")
    private TaskStatusType statusType;

    @Column(nullable = false)
    private String name;
}
