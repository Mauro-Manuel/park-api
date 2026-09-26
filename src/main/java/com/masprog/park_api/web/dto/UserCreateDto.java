package com.masprog.park_api.web.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCreateDto {

    private String username;
    private String password;
}
