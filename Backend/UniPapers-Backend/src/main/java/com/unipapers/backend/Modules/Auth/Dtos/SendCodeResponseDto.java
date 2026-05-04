package com.unipapers.backend.Modules.Auth.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCodeResponseDto {

    private String email;
    private String message;
}

