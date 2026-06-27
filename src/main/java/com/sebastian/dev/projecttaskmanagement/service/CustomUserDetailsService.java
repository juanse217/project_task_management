package com.sebastian.dev.projecttaskmanagement.service;

import com.sebastian.dev.projecttaskmanagement.repository.UserRepository;
import com.sebastian.dev.projecttaskmanagement.repository.entities.Role;
import com.sebastian.dev.projecttaskmanagement.repository.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "The user with username " + username + " not found")); // Handle
                                                                                                       // automatically.
                                                                                                       // Can be
                                                                                                       // customized if
                                                                                                       // wanted
                String[] roles = user.getRoles()
                                .stream()
                                .map(Role::name)
                                .toArray(String[]::new);
                return User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .roles(roles)
                                .build();

        }
}
