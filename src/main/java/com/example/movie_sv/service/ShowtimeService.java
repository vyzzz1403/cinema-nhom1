package com.example.movie_sv.service;

import com.example.movie_sv.model.Showtime;
import com.example.movie_sv.repository.ShowtimeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ShowtimeService(ShowtimeRepository showtimeRepository, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.showtimeRepository = showtimeRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // ĐỌC: Lấy lịch chiếu của phim theo ngày có Cache Redis (TTL 5 phút)
    public List<Showtime> getShowtimesByMovieAndDate(Long movieId, LocalDate showDate) {
        // Đổi LocalDate thành chuỗi (vd: "2026-10-25") để làm key trên Redis
        String dateString = showDate.toString(); 
        String cacheKey = "showtimes:movie:" + movieId + ":" + dateString;
        
        try {
            String cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                return objectMapper.readValue(cachedData, new TypeReference<List<Showtime>>(){});
            }

            // Gọi đúng hàm đã được đồng bộ tên trong Repository
            List<Showtime> showtimes = showtimeRepository.findByMovieIdAndShowDate(movieId, showDate);

            String jsonVal = objectMapper.writeValueAsString(showtimes);
            redisTemplate.opsForValue().set(cacheKey, jsonVal, Duration.ofMinutes(5));

            return showtimes;
        } catch (Exception e) {
            return showtimeRepository.findByMovieIdAndShowDate(movieId, showDate);
        }
    }

    // GHI: Xóa cache khi Admin thêm/sửa/xóa suất chiếu
    public void clearShowtimeCache(Long movieId, LocalDate showDate) {
        String dateString = showDate.toString();
        redisTemplate.delete("showtimes:movie:" + movieId + ":" + dateString);
    }
}