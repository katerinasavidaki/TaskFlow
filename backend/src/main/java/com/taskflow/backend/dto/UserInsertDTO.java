package com.taskflow.backend.dto;

import com.taskflow.backend.core.enums.RoleType;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInsertDTO {

    @NotBlank(message = "Firstname is required")
    @Size(min = 4, message = "Firstname must be at least 4 characters long")
    private String firstname;

    @NotBlank(message = "Lastname is required")
    @Size(min = 4, message = "Lastname must be at least 4 characters long")
    private String lastname;

    @NotBlank(message = "AFM is required")
    @Pattern(regexp = "^\\d{9,}$", message = "AFM must be at least 9 digits")
    @Size(min = 9, message = "AFM must be at least 9 digits")
    private String afm;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10,}$", message = "Phone number must be at least 10 digits")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Username is required")
    @Email(message = "Invalid username")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid username format")
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$",
            message = "Invalid Password")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$",
            message = "Invalid Password")
    private String confirmPassword;

    @NotNull(message = "Role is required")
    private RoleType role;

    private Boolean isActive = true; // Default to true if not specified

    private Long teamId;
}
