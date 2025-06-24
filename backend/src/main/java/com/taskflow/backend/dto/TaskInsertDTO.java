package com.taskflow.backend.dto;

import com.taskflow.backend.core.enums.TaskPriorityType;
import com.taskflow.backend.core.enums.TaskStatusType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskInsertDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, message = "Title must contain at least 3 characters")
    private String title;

    @Size(min = 5, message = "Description must contain at least 5 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private TaskPriorityType priority;

    @NotNull(message = "Status is required")
    private TaskStatusType status;

    private LocalDate dueDate;

    private Boolean isCompleted = false;

    private Long assignedToId;

    private Long teamId;
}
