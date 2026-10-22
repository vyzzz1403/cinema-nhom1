package com.example.movie_sv.controller;

import com.example.movie_sv.dto.AuthResponse;
import com.example.movie_sv.dto.LoginRequest;
import com.example.movie_sv.dto.RegisterRequest;
import com.example.movie_sv.model.Role;
import com.example.movie_sv.model.User;
import com.example.movie_sv.repository.UserRepository;
import com.example.movie_sv.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Thêm bộ mã hóa mật khẩu

    // Cập nhật constructor để nhận PasswordEncoder
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // Kiểm tra tài khoản và mật khẩu
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Lấy thông tin user từ DB để lấy Role
        User user = userRepository.findByUsername(request.getUsername()).get();

        // Tạo JWT Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(new AuthResponse(token));
    }

    // Bổ sung API Đăng ký
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Kiểm tra xem username đã tồn tại trong DB chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Lỗi: Tên đăng nhập đã tồn tại!");
        }

        // Tạo tài khoản mới, mã hóa mật khẩu và gán quyền CUSTOMER mặc định
        User newUser = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.CUSTOMER
        );

        userRepository.save(newUser);
        return ResponseEntity.ok("Đăng ký tài khoản thành công!");
    }
}