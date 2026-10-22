package com.example.movie_sv.security;

import com.example.movie_sv.model.Role;
import com.example.movie_sv.model.User;
import com.example.movie_sv.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu bảng users chưa có dữ liệu thì mới tạo
        if (userRepository.count() == 0) {
            // Tạo tài khoản Admin
            User admin = new User("admin", passwordEncoder.encode("123456"), Role.ADMIN);
            
            // Tạo tài khoản Khách hàng
            User customer = new User("customer01", passwordEncoder.encode("123456"), Role.CUSTOMER);
            
            userRepository.save(admin);
            userRepository.save(customer);
            
            System.out.println("====== ĐÃ TẠO TÀI KHOẢN MẪU THÀNH CÔNG ======");
        }
    }
}
