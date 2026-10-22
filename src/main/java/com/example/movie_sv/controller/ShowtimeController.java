package com.example.movie_sv.controller;

import com.example.movie_sv.model.Showtime;
import com.example.movie_sv.service.ShowtimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    // API Lấy lịch chiếu theo ngày (Đã tích hợp Redis Cache 5 phút)
    @GetMapping
    public ResponseEntity<List<Showtime>> getShowtimesByMovieAndDate(
            @RequestParam Long movieId,
            @RequestParam String date) {
        
        // Chuyển đổi chuỗi ngày "2026-10-23" thành kiểu LocalDate
        LocalDate showDate = LocalDate.parse(date);
        
        // Gọi Service lấy dữ liệu
        List<Showtime> showtimes = showtimeService.getShowtimesByMovieAndDate(movieId, showDate);
        return ResponseEntity.ok(showtimes);
    }
}
