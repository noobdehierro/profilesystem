package com.example.profilesystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Bean
    // SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // http
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers("/admin/**").hasRole("ADMIN")
    // .anyRequest().permitAll())
    // .formLogin(form -> form
    // .defaultSuccessUrl("/admin/qrs", true))
    // .logout(logout -> logout.logoutSuccessUrl("/"));

    // return http.build();
    // }
}
