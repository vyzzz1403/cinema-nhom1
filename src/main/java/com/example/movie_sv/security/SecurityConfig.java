package com.example.movie_sv.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Bật tính năng phân quyền bằng @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // CẬP NHẬT: Mở khóa API xác thực và API Test khởi tạo dữ liệu
                .requestMatchers("/api/v1/auth/**", "/api/v1/test/**").permitAll()
                
                // BỔ SUNG: Mở khóa TOÀN BỘ các API GET để khách hàng xem phim, rạp, suất chiếu, ghế mà không cần đăng nhập
                .requestMatchers(HttpMethod.GET, "/api/v1/movies/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/cinemas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/showtimes/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/rooms/**").permitAll()
                
                // CẬP NHẬT: API Đặt vé mới (tickets) bắt buộc phải đăng nhập (có Token)
                .requestMatchers(HttpMethod.POST, "/api/v1/tickets").authenticated()
                
                // Các API khác (ví dụ Thêm/Sửa/Xóa của Admin) bắt buộc yêu cầu đăng nhập
                .anyRequest().authenticated() 
            )
            // Lắp đặt Trạm kiểm soát Token vào trước trạm kiểm tra Username/Password mặc định
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
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