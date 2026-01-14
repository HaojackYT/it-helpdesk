package com.ithd.it_helpdesk.config;

import com.ithd.it_helpdesk.security.CustomAccessDeniedHandler;
import com.ithd.it_helpdesk.security.JwtAuthenticationEntryPoint;
import com.ithd.it_helpdesk.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Static resources & Login page
                .requestMatchers("/", "/login", "/login.html", "/signup", "/signup.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**").permitAll()
                
                // Dashboard pages - allow access (authorization handled by frontend)
                .requestMatchers("/admin/dashboard", "/admin/**").permitAll()
                .requestMatchers("/support/dashboard", "/support/**").permitAll()
                .requestMatchers("/employee/dashboard", "/employee/**").permitAll()
                .requestMatchers("/dashboard").permitAll()
                
                // User Management - requires specific permissions
                    .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                    // Allow authenticated users to update their own profile without USER_UPDATE authority
                    .requestMatchers(HttpMethod.PUT, "/api/users/me").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/users/count").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("USER_VIEW")
                .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("USER_CREATE")
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAuthority("USER_UPDATE")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAuthority("USER_DELETE")
                .requestMatchers(HttpMethod.POST, "/api/users/{id}/roles").hasAuthority("ROLE_ASSIGN")
                
                // Ticket Management
                .requestMatchers(HttpMethod.POST, "/api/tickets").hasAuthority("TICKET_CREATE")
                .requestMatchers(HttpMethod.GET, "/api/tickets/my").hasAnyAuthority("TICKET_VIEW_OWN", "TICKET_VIEW_ALL")
                .requestMatchers(HttpMethod.GET, "/api/tickets").hasAuthority("TICKET_VIEW_ALL")
                .requestMatchers(HttpMethod.PUT, "/api/tickets/{id}/assign").hasAuthority("TICKET_ASSIGN")
                .requestMatchers(HttpMethod.PUT, "/api/tickets/{id}/status").hasAuthority("TICKET_UPDATE_STATUS")
                .requestMatchers(HttpMethod.DELETE, "/api/tickets/{id}").hasAuthority("TICKET_DELETE")
                
                // Comments
                .requestMatchers(HttpMethod.POST, "/api/tickets/{id}/comments").hasAuthority("COMMENT_ADD")
                
                // Reports
                .requestMatchers(HttpMethod.GET, "/api/reports/**").hasAuthority("REPORT_VIEW")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
