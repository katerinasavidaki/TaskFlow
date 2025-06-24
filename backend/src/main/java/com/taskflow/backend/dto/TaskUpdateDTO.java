package com.taskflow.backend.dto;

import com.taskflow.backend.core.enums.TaskPriorityType;
import com.taskflow.backend.core.enums.TaskStatusType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskUpdateDTO {

    @Size(min = 3, message = "Title must contain at least 3 characters")
    private String title;

    @Size(min = 5, message = "Description must contain at least 5 characters")
    private String description;

    private TaskStatusType status;

    private TaskPriorityType priority;

    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDate dueDate;

    private Boolean isCompleted;

    private Long assignedToId;

    private Long teamId;
}
