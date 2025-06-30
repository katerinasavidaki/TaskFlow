package com.taskflow.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamReadOnlyDTO {

    private Long id;
    private String name;
    private String managerFullName;
    private Set<String> memberFullNames = new HashSet<>();

}
