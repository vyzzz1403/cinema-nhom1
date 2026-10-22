package com.example.movie_sv.service;

import com.example.movie_sv.model.Seat;
import com.example.movie_sv.repository.SeatRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public SeatService(SeatRepository seatRepository, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.seatRepository = seatRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // ĐỌC: Lấy danh sách toàn bộ ghế của một phòng chiếu có Cache Redis (TTL 60 phút)[cite: 9]
    public List<Seat> getSeatsByRoomId(Long roomId) {
        String cacheKey = "screen:" + roomId + ":seats";
        try {
            // Bước 1: Tìm trong Redis trước
            String cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                return objectMapper.readValue(cachedData, new TypeReference<List<Seat>>(){});
            }

            // Bước 2: Nếu không có, truy vấn SQL
            List<Seat> seats = seatRepository.findByRoomId(roomId);

            // Bước 3: Lưu vào Redis với TTL 60 phút[cite: 9]
            String jsonVal = objectMapper.writeValueAsString(seats);
            redisTemplate.opsForValue().set(cacheKey, jsonVal, Duration.ofMinutes(60));

            return seats;
        } catch (Exception e) {
            // Dự phòng nếu Redis lỗi, trả về trực tiếp từ SQL
            return seatRepository.findByRoomId(roomId);
        }
    }

    // GHI: Khi Admin thêm/sửa/xóa sơ đồ ghế của phòng, phải xóa cache này[cite: 9]
    public void clearScreenSeatsCache(Long roomId) {
        redisTemplate.delete("screen:" + roomId + ":seats");
    }
}