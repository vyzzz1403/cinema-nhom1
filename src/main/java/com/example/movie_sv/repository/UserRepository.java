package com.example.movie_sv.repository;

import com.example.movie_sv.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Hàm dùng để tìm User trong DB dựa vào username khi đăng nhập
    Optional<User> findByUsername(String username);
}
