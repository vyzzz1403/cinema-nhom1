package com.example.movie_sv.repository;

import com.example.movie_sv.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(String title, String genre, Pageable pageable);
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Movie> findByGenreContainingIgnoreCase(String genre, Pageable pageable);
    
    // THÊM MỚI: Tìm phim theo trạng thái (Đang chiếu / Sắp chiếu)
    List<Movie> findByStatus(String status);
}