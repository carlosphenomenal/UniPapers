package com.unipapers.backend.Modules.Auth.Dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCodeRequestDto {

    @NotBlank(message = "Email or student number is required")
    private String identifier;
}

