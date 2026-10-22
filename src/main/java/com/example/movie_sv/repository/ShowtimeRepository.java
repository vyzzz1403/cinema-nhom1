package com.example.movie_sv.repository;

import com.example.movie_sv.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    
    // Lấy tất cả lịch chiếu của một phim
    List<Showtime> findByMovieId(Long movieId); 

    // Lấy lịch chiếu theo phim VÀ ngày chiếu (Đã khớp tên biến showDate)
    List<Showtime> findByMovieIdAndShowDate(Long movieId, LocalDate showDate);
}