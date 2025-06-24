package com.taskflow.backend.dto;

import com.taskflow.backend.core.enums.RoleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateDTO {


    @Size(min = 4, message = "Firstname must be at least 4 characters long")
    private String firstname;

    @Size(min = 4, message = "Firstname must be at least 4 characters long")
    private String lastname;

    @Pattern(regexp = "^\\d{10,}$", message = "Phone number must be at least 10 digits")
    @Size(min = 10, message = "Phone number must be at least 10 digits")
    private String phoneNumber;

    @Pattern(regexp = "^\\d{9,}$", message = "AFM must be at least 9 digits")
    @Size(min = 9, message = "AFM must be at least 9 digits")
    private String afm;

    private RoleType role;

    private Boolean isActive;

    private Long teamId;
}
