package com.example.movie_sv.repository;

import com.example.movie_sv.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    // Bổ sung hàm này để tìm toàn bộ ghế thuộc về một phòng (Room)
    List<Seat> findByRoomId(Long roomId);
}