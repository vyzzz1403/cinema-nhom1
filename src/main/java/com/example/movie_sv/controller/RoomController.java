package com.example.movie_sv.controller;

import com.example.movie_sv.model.Seat;
import com.example.movie_sv.repository.SeatRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final SeatRepository seatRepository;

    public RoomController(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Operation(summary = "Lấy sơ đồ ghế của một phòng chiếu")
    @GetMapping("/v1/rooms/{id}/seats")
    public ResponseEntity<List<Seat>> getSeatsByRoomId(@PathVariable Long id) {
        // Lấy danh sách ghế từ database dựa vào ID của phòng
        List<Seat> seats = seatRepository.findByRoomId(id);
        return ResponseEntity.ok(seats);
    }
}