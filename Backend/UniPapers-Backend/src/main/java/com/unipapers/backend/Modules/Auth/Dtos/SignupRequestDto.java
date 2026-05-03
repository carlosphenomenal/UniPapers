package com.unipapers.backend.Modules.Auth.Dtos;

import com.unipapers.backend.Common.Validators.MakerereEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @MakerereEmail(message = "This is not a valid Makerere email")
    private String email;

    @NotNull(message = "Student number is required")
    @Positive(message = "Student number must be positive")
    private long studentNumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Programme public ID is required")
    private String programmePublicId;

    @Positive(message = "Year of study must be positive")
    private int yearOfStudy;
}
