package com.taskflow.backend.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamReadOnlyDTO {

    private Long id;
    private String name;
    private String managerFullName;
    private Set<String> memberFullNames = new HashSet<>();

}
