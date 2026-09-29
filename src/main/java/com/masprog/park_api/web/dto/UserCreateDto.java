package com.masprog.park_api.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCreateDto {

    @NotBlank(message = "Email is required.")
    @Email( message = "The email format is invalid.", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 6, message = "Password must be exactly 6 characters long.")
    private String password;
}
