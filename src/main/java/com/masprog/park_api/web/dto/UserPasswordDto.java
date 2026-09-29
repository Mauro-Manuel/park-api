package com.masprog.park_api.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserPasswordDto {

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 6, message = "Password must be exactly 6 characters long.")
    private String currentPassword;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 6, message = "Password must be exactly 6 characters long.")
    private String newPassword;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 6, message = "Password must be exactly 6 characters long.")
    private String confirmPassword;
}
