package com.example.movie_sv.controller;

import com.example.movie_sv.model.*;
import com.example.movie_sv.repository.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final CinemaRepository cinemaRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    public TestController(CinemaRepository cinemaRepository, RoomRepository roomRepository,
                          SeatRepository seatRepository, MovieRepository movieRepository,
                          ShowtimeRepository showtimeRepository) {
        this.cinemaRepository = cinemaRepository;
        this.roomRepository = roomRepository;
        this.seatRepository = seatRepository;
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @PostMapping("/init")
    public String initData() {
        // 1. Tạo Rạp
        Cinema cinema = new Cinema();
        cinema.setName("CGV Vincom");
        cinema.setAddress("Hà Nội");
        cinemaRepository.save(cinema);

        // 2. Tạo Phòng
        Room room = new Room();
        room.setName("Phòng 1");
        room.setCapacity(50);
        room.setCinema(cinema);
        roomRepository.save(room);

        // 3. Tạo 2 Ghế (1 Thường, 1 VIP)
        Seat seat1 = new Seat(); seat1.setSeatName("A1"); seat1.setSeatType(SeatType.STANDARD); seat1.setRoom(room);
        Seat seat2 = new Seat(); seat2.setSeatName("VIP1"); seat2.setSeatType(SeatType.VIP); seat2.setRoom(room);
        seatRepository.save(seat1);
        seatRepository.save(seat2);

        // 4. Tạo Phim
        Movie movie = new Movie();
        movie.setTitle("Phim Test Hệ Thống Mới");
        movie.setGenre("Hành động");
        movie.setReleaseYear(2026); 
        movie.setStatus("Đang chiếu"); 
        movieRepository.save(movie);

        // 5. Tạo Suất chiếu (Giá gốc 80k)
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setBasePrice(80000.0); // Đã thêm .0 để khớp kiểu Double
        
        // Đã tách riêng ngày chiếu và giờ chiếu cho khớp với Model Showtime
        showtime.setShowDate(LocalDate.now().plusDays(1));
        showtime.setStartTime(LocalTime.now());
        
        showtimeRepository.save(showtime);

        return "Khởi tạo dữ liệu thành công! ID Suất chiếu: " + showtime.getId() + 
               " | ID Ghế thường: " + seat1.getId() + " | ID Ghế VIP: " + seat2.getId();
    }
}