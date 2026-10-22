package com.example.movie_sv.controller;

import com.example.movie_sv.model.Seat;
import com.example.movie_sv.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@Tag(name = "Seat API", description = "Quản lý sơ đồ ghế ngồi")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @Operation(summary = "Lấy sơ đồ ghế theo ID Phòng chiếu")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Seat>> getSeatsByRoomId(@PathVariable Long roomId) {
        List<Seat> seats = seatService.getSeatsByRoomId(roomId);
        return ResponseEntity.ok(seats);
    }
}