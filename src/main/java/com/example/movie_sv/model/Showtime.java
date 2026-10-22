package com.example.movie_sv.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // Đổi tên chuẩn thành showDate để tránh trùng từ khóa SQL và lỗi JPA
    private LocalDate showDate; 
    private LocalTime startTime;
    private LocalTime endTime;

    // Bổ sung thuộc tính giá vé gốc
    private Double basePrice;

    // Các hàm Getter và Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public LocalDate getShowDate() { return showDate; }
    public void setShowDate(LocalDate showDate) { this.showDate = showDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    // Bổ sung Getter và Setter cho basePrice để TicketService có thể gọi được
    public Double getBasePrice() { 
        return basePrice != null ? basePrice : 0.0; 
    }
    public void setBasePrice(Double basePrice) { 
        this.basePrice = basePrice; 
    }
}