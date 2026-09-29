package com.masprog.park_api.service;

import com.masprog.park_api.entity.User;
import com.masprog.park_api.exception.UsernameUniqueViolationException;
import com.masprog.park_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {

        try {
            return userRepository.save(user);

        } catch (DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("Username %s already exists", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    @Transactional
    public User changePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {

        if(!newPassword.equals(confirmPassword)){
           throw new RuntimeException("New password does not match the password confirmation.");
        }

        User user = findById(id);
        if (!user.getPassword().equals(currentPassword)){
            throw new RuntimeException("Your password does not match.");
        }
        user.setPassword(newPassword);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
