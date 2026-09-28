package com.masprog.park_api.web.controller;

import com.masprog.park_api.entity.User;
import com.masprog.park_api.service.UserService;
import com.masprog.park_api.web.dto.UserCreateDto;
import com.masprog.park_api.web.dto.UserPasswordDto;
import com.masprog.park_api.web.dto.UserResponseDto;
import com.masprog.park_api.web.dto.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody UserCreateDto createDto){
       User user = userService.save(UserMapper.toUser(createDto));
       return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id){
        User user = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody UserPasswordDto userPasswordDto){
        User userChange = userService.changePassword(id, userPasswordDto.getCurrentPassword(),
                userPasswordDto.getNewPassword(), userPasswordDto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll(){
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

}
