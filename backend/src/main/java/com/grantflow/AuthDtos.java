package com.grantflow;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank String fullName,
            @Email String email,
            @NotBlank String password,
            Role role
    ) {}

    public record LoginRequest(
            @Email String email,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String token,
            Long userId,
            String fullName,
            String email,
            Role role
    ) {}
}
