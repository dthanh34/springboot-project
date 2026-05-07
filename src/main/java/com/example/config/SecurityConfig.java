package com.example.config;

import com.example.repository.UserRepository;
import com.example.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_RESOURCES = {
            "/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**", "/assets/**"
    };

    private static final String[] PUBLIC_URLS = {
            "/", "/login", "/register/**", "/api/auth/**", "/error"
    };

   
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {
        return email -> {
            User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

            return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole()) 
                .build();
        };
    }

    
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserRepository userRepo) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService(userRepo));
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_RESOURCES).permitAll()
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers("/api/admin/foods").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/home", "/profile", "/foods", "/meal-plan", "/progress", 
                                "/favorites", "/search", "/customize-recipe", "/settings", "/food-detail")
                                .hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable()) 
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}