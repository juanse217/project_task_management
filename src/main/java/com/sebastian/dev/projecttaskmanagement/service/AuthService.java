package com.sebastian.dev.projecttaskmanagement.service;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sebastian.dev.projecttaskmanagement.exception.businessviolation.UsernameAlreadyInUserException;
import com.sebastian.dev.projecttaskmanagement.repository.UserRepository;
import com.sebastian.dev.projecttaskmanagement.repository.entities.Role;
import com.sebastian.dev.projecttaskmanagement.repository.entities.UserEntity;
import com.sebastian.dev.projecttaskmanagement.security.JwtUtil;
import com.sebastian.dev.projecttaskmanagement.shared.AuthResponse;
import com.sebastian.dev.projecttaskmanagement.shared.LoginRequest;
import com.sebastian.dev.projecttaskmanagement.shared.RegisterRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        String username = request.username();
        if(userRepository.existsByUsername(username)){
            throw new UsernameAlreadyInUserException("The username " + username + "is already in use");
        }

        Set<Role> roles = Set.of(Role.PROJECT_USER);
        
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(roles);

        userRepository.save(user);

        String token = jwtUtil.generateToken(username, roles);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User " + request.username() + " not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());

        return new AuthResponse(token);
    }
}
