package com.ithd.it_helpdesk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    
    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private String department;
    private Set<String> roles;
    private String message;
}
