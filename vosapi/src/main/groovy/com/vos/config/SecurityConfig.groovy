package com.vos.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints for vendor onboarding
                    .requestMatchers("/api/v1/vendor-sessions/**").permitAll()
                    .requestMatchers("/api/v1/vendors/**/onboarding").permitAll()
                    .requestMatchers("/api/v1/vendors/**/files").permitAll()
                    // Procurement endpoints require authentication
                    .requestMatchers("/api/v1/vendor-requests/**").hasRole("PROCUREMENT")
                    .requestMatchers("/api/v1/vendors/**").hasRole("PROCUREMENT")
                    .requestMatchers("/api/v1/follow-ups/**").hasRole("PROCUREMENT")
                    .anyRequest().authenticated()
            }
            .httpBasic { }
        
        http.build()
    }
    
    @Bean
    UserDetailsService userDetailsService() {
        UserDetails procurementUser = User.builder()
            .username("procurement")
            .password(passwordEncoder().encode("procurement123"))
            .roles("PROCUREMENT")
            .build()
        
        new InMemoryUserDetailsManager(procurementUser)
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        new BCryptPasswordEncoder()
    }
}

