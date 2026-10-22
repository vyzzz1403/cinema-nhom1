package com.example.movie_sv.service;

import com.example.movie_sv.model.*;
import com.example.movie_sv.repository.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final StringRedisTemplate redisTemplate; // Khai báo Redis Template

    // Tiêm StringRedisTemplate vào Constructor
    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, 
                         ShowtimeRepository showtimeRepository, SeatRepository seatRepository,
                         StringRedisTemplate redisTemplate) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.redisTemplate = redisTemplate;
    }

    // MỤC 6.2: KHÓA GHẾ CÓ TTL (600 GIÂY)
    public boolean lockSeat(Long showtimeId, Long seatId, Long userId) {
        String lockKey = "seat_lock:" + showtimeId + ":" + seatId;
        String lockValue = String.valueOf(userId);
        
        // NX: setIfAbsent (Chỉ tạo khi khóa chưa tồn tại)
        // EX: Duration.ofSeconds(600) (Tự động xóa sau 600 giây)
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, Duration.ofSeconds(600));
        return Boolean.TRUE.equals(isLocked);
    }

    public Ticket createTicket(Long userId, Long showtimeId, Long seatId) {
        // 1. Kiểm tra khóa ghế trên Redis trước khi xử lý DB
        boolean isSeatLocked = lockSeat(showtimeId, seatId, userId);
        if (!isSeatLocked) {
            throw new RuntimeException("Ghế này đang được người khác giữ hoặc đã bán! Vui lòng chọn ghế khác.");
        }

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
            Showtime showtime = showtimeRepository.findById(showtimeId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Suất chiếu"));
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Ghế"));

            // 2. Kiểm tra xem ghế đã bị đặt trong DB chưa (dự phòng)
            List<Ticket> existingTickets = ticketRepository.findByShowtimeId(showtimeId);
            boolean isSeatTaken = existingTickets.stream()
                    .anyMatch(t -> t.getSeat().getId().equals(seatId) && t.getStatus() != TicketStatus.CANCELLED);
            
            if (isSeatTaken) {
                throw new RuntimeException("Ghế này đã có người đặt trong hệ thống!");
            }

            // 3. Tính toán giá vé = Giá gốc + Phụ thu loại ghế
            double surcharge = 0;
            if (seat.getSeatType() == SeatType.VIP) {
                surcharge = 20000; // VIP phụ thu 20k
            } else if (seat.getSeatType() == SeatType.SWEETBOX) {
                surcharge = 50000; // Sweetbox phụ thu 50k
            }
            double finalPrice = showtime.getBasePrice() + surcharge;

            // 4. Tạo vé mới
            Ticket ticket = new Ticket();
            ticket.setUser(user);
            ticket.setShowtime(showtime);
            ticket.setSeat(seat);
            ticket.setFinalPrice(finalPrice);
            ticket.setStatus(TicketStatus.PENDING); // Trạng thái chờ thanh toán

            return ticketRepository.save(ticket);
            
        } catch (Exception e) {
            // Nếu có lỗi xảy ra (ví dụ: Không tìm thấy DB, lỗi mạng), bắt buộc phải nhả ghế trên Redis
            redisTemplate.delete("seat_lock:" + showtimeId + ":" + seatId);
            throw e;
        }
    }

    public List<Ticket> getTicketsByUserId(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    // ĐỌC: Lấy tất cả danh sách vé (dành cho Admin)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // ĐỌC: Lấy chi tiết một vé cụ thể theo ID
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé với ID này!"));
    }

    // SỬA: Cập nhật trạng thái vé
    public Ticket updateTicketStatus(Long id, TicketStatus newStatus) {
        Ticket ticket = getTicketById(id);
        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }

    // XÓA: Xóa vé
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy vé để xóa!");
        }
        ticketRepository.deleteById(id);
    }
}