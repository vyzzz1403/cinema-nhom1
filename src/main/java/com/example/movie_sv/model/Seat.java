package com.example.movie_sv.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String seatName; // Ví dụ: A1, A2, G10

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public Seat() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSeatName() { return seatName; }
    public void setSeatName(String seatName) { this.seatName = seatName; }
    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}