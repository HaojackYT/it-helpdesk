package com.ithd.it_helpdesk.service;

import com.ithd.it_helpdesk.dto.request.LoginRequest;
import com.ithd.it_helpdesk.dto.request.RegisterRequest;
import com.ithd.it_helpdesk.dto.response.LoginResponse;
import com.ithd.it_helpdesk.dto.response.RegisterResponse;
import com.ithd.it_helpdesk.entity.Role;
import com.ithd.it_helpdesk.entity.User;
import com.ithd.it_helpdesk.exception.DuplicateResourceException;
import com.ithd.it_helpdesk.exception.ResourceNotFoundException;
import com.ithd.it_helpdesk.repository.RoleRepository;
import com.ithd.it_helpdesk.repository.UserRepository;
import com.ithd.it_helpdesk.security.CustomUserDetails;
import com.ithd.it_helpdesk.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .collect(Collectors.toSet());
        
        Set<String> permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.startsWith("ROLE_"))
                .collect(Collectors.toSet());

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .username(userDetails.getUsername())
                .fullName(userDetails.getFullName())
                .email(userDetails.getEmail())
                .department(userDetails.getDepartment())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        // Get default role (EMPLOYEE)
        Role employeeRole = roleRepository.findByName(Role.RoleName.ROLE_EMPLOYEE)
                .orElseThrow(() -> new ResourceNotFoundException("Employee role not found"));

        // Create new user
        Set<Role> roles = new HashSet<>();
        roles.add(employeeRole);

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .department(request.getDepartment())
                .status(User.UserStatus.ACTIVE)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(newUser);

        // Map roles to role names
        Set<String> roleNames = savedUser.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .department(savedUser.getDepartment())
                .roles(roleNames)
                .message("Registration successful. You can now login with your credentials.")
                .build();
    }
}
