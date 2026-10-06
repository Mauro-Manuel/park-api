package com.masprog.park_api.service;

import com.masprog.park_api.entity.User;
import com.masprog.park_api.exception.EntityNotFoundException;
import com.masprog.park_api.exception.PasswordInvalidException;
import com.masprog.park_api.exception.UsernameUniqueViolationException;
import com.masprog.park_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(User user) {

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);

        } catch (DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("Username %s already exists", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%s not found", id))
        );
    }

    @Transactional
    public User changePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {

        if(!newPassword.equals(confirmPassword)){
           throw new PasswordInvalidException("New password does not match the password confirmation.");
        }

        User user = findById(id);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new PasswordInvalidException("Your password does not match.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }


    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with 'username' not found", username))
        );
    }

    public User.Role findRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }
}
