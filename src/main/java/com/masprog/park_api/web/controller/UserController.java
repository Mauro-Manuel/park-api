package com.masprog.park_api.web.controller;

import com.masprog.park_api.entity.User;
import com.masprog.park_api.service.UserService;
import com.masprog.park_api.web.dto.UserCreateDto;
import com.masprog.park_api.web.dto.UserPasswordDto;
import com.masprog.park_api.web.dto.UserResponseDto;
import com.masprog.park_api.web.dto.mapper.UserMapper;
import com.masprog.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(
        name = "User",
        description = "Operations for creating, updating, and retrieving users."
)
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "A user with the provided username or email already exists.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "The request contains invalid input data.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto createDto){
       User user = userService.save(UserMapper.toUser(createDto));
       return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(user));
    }

    @Operation(summary = "Find user by ID", description = "Retrieves a user by their unique identifier.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "User retrieved successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id){
        User user = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDto userPasswordDto){
        User userChange = userService.changePassword(id, userPasswordDto.getCurrentPassword(),
                userPasswordDto.getNewPassword(), userPasswordDto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll(){
        List<User> users = userService.findAll();
        return ResponseEntity.ok(UserMapper.toListDto(users));
    }

}
