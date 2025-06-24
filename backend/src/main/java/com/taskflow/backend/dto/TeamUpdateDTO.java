package com.taskflow.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamUpdateDTO {

    @Size(min = 3, message = "Team name must contain at least 3 characters")
    private String name;

    private Long managerId;

    private Set<Long> memberIds = new HashSet<>();
}
