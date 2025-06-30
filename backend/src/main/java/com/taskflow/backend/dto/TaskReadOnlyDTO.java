package com.taskflow.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskReadOnlyDTO {

    private Long id;
    private String title;
    private String description;
    private String priority;    // Enum as string (e.g "HIGH")
    private String status;      // Enum as string (e.g "DONE")
    private LocalDate dueDate;
    private Boolean isCompleted;
//    private String createdByUsername;
    private String assignedToUsername;
    private String teamName;
}
