package com.rentvideo.app.config;

import com.rentvideo.app.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // enables @PreAuthorize / @PostAuthorize if you want to use them later
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Password encoder used for hashing passwords when registering users.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider that uses your CustomUserDetailsService and BCrypt
     * encoder.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider prov = new DaoAuthenticationProvider(userDetailsService);
        prov.setPasswordEncoder(passwordEncoder());
        return prov;
    }

    /**
     * Security filter chain:
     * - /api/auth/** is public (registration)
     * - /api/videos/admin/** requires ROLE_ADMIN
     * - /api/videos/** requires any authenticated user
     * - other requests require authentication by default
     * - Basic Auth is enabled (HTTP Basic challenge)
     * - stateless session management for typical REST behavior
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF for a stateless REST API (adjust if you use cookies)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/videos/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/videos/**").authenticated()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults()) // enable Basic Auth prompt
                .authenticationProvider(daoAuthenticationProvider());

        return http.build();
    }
}
