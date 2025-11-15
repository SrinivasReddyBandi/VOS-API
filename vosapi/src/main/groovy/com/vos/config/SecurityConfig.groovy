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
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.http.HttpMethod

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS and use our CorsConfigurationSource bean
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // Allow preflight requests
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // Public endpoints for vendor onboarding
                    .requestMatchers("/api/v1/vendor-sessions/**").permitAll()
                        .requestMatchers("/api/v1/vendors/{vendorId}/onboarding").permitAll()
                        .requestMatchers("/api/v1/vendors/{vendorId}/files").permitAll()
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

    // CORS configuration bean - currently permissive for development. Restrict origins in production.
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration()
        // Allow all origins (use allowedOriginPatterns so credentials can be supported). Change to specific origins as needed.
        config.allowedOriginPatterns = ["*"]
        config.allowedMethods = ["GET","POST","PUT","DELETE","OPTIONS","PATCH"]
        config.allowedHeaders = ["*"]
        config.exposedHeaders = ["Authorization","Link","X-Total-Count"]
        config.allowCredentials = true
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}
