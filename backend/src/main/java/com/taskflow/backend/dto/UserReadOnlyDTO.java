package com.taskflow.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taskflow.backend.core.enums.RoleType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserReadOnlyDTO {

    private Long id;
    private String uuid;
    private String firstname;
    private String lastname;
    private String afm;
    private String phoneNumber;
    private String username;
    private String role;
    private Boolean isActive;
    private String teamName;
}
