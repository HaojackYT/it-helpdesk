package com.ithd.it_helpdesk.dto.response;

import com.ithd.it_helpdesk.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private UUID id;
    private String username;
    private String fullName;
    private String email;
    private String department;
    private User.UserStatus status;
    private LocalDateTime createdAt;
    private Set<String> roles;
}
