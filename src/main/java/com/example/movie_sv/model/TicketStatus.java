package com.example.movie_sv.model;

public enum TicketStatus {
    PENDING,   // Đang giữ ghế, chờ thanh toán
    PAID,      // Đã thanh toán thành công
    CANCELLED  // Đã hủy (do quá hạn thanh toán)
}