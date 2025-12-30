package com.ithd.it_helpdesk.controller;

import com.ithd.it_helpdesk.dto.request.LoginRequest;
import com.ithd.it_helpdesk.dto.response.ApiResponse;
import com.ithd.it_helpdesk.dto.response.LoginResponse;
import com.ithd.it_helpdesk.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // For stateless JWT, logout is handled on client side by removing the token
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }
}
