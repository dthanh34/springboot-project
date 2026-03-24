package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Định nghĩa thuật toán mã hóa mật khẩu (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Cấu hình phân quyền truy cập
 @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) 
        .authorizeHttpRequests(auth -> auth
            // 1. Cho phép tất cả mọi người vào các trang này
            .requestMatchers(
                "/", "/login", "/register/**", "/api/auth/**", 
                "/css/**", "/js/**", "/images/**"
            ).permitAll() 
            
            // 2. MỞ KHÓA CHO TRANG USER VÀ ADMIN (QUAN TRỌNG NHẤT)
            .requestMatchers("/user/**").hasAnyAuthority("USER", "ADMIN")
            .requestMatchers("/admin/**").hasAuthority("ADMIN")
            
            // 3. Các yêu cầu khác phải đăng nhập
            .anyRequest().authenticated() 
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )
        .formLogin(login -> login
            .loginPage("/login") 
            .permitAll()
        )
        // Thêm Logout để xóa session khi cần
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login")
            .permitAll()
        );

    return http.build();
}
}