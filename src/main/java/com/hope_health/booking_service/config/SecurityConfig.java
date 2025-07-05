package com.hope_health.booking_service.config;

import com.hope_health.booking_service.util.JwtAuthConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/bookings/test").permitAll() // Example public endpoint
                .requestMatchers("/api/*").permitAll() // Example public endpoint
                .anyRequest().authenticated()
            )
            .cors(Customizer.withDefaults());

        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthConverter);

        http.headers(headers -> headers.frameOptions().disable()); // Disable frame options if needed
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }
}
