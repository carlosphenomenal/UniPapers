package com.unipapers.backend.Modules.Auth.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private long studentNumber;
    private String password;
    private String programmePublicId;
    private int yearOfStudy;
}
