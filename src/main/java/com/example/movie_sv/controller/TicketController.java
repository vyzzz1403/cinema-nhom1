package com.example.movie_sv.controller;

import com.example.movie_sv.dto.TicketRequest;
import com.example.movie_sv.model.Ticket;
import com.example.movie_sv.model.User;
import com.example.movie_sv.repository.UserRepository;
import com.example.movie_sv.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Ticket API", description = "Quản lý đặt vé nâng cao")
public class TicketController {

    private final TicketService ticketService;
    private final UserRepository userRepository; // Tiêm thêm UserRepository để tra cứu thông tin User

    public TicketController(TicketService ticketService, UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "1. Đặt vé mới (Tự động lấy User từ Token)")
    @PostMapping("/tickets")
    public ResponseEntity<?> bookTicket(@RequestBody TicketRequest request, Authentication authentication) {
        try {
            // Lấy username từ Token đang đăng nhập
            String currentUsername = authentication.getName();
            // Tìm User trong Database để lấy ra ID chuẩn xác nhất
            User currentUser = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

            // BỎ QUA userId truyền từ Body. Dùng thẳng currentUser.getId() để tạo vé
            Ticket newTicket = ticketService.createTicket(
                    currentUser.getId(), 
                    request.getShowtimeId(), 
                    request.getSeatId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(newTicket);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "2. Lấy danh sách vé của TÔI (Không cần nhập ID)")
    @GetMapping("/tickets/my-tickets")
    public ResponseEntity<?> getMyTickets(Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        // Tự động tìm vé dựa trên ID của Token
        List<Ticket> tickets = ticketService.getTicketsByUserId(currentUser.getId());
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "3. Lấy danh sách vé theo User ID (Có kiểm tra quyền)")
    @GetMapping("/users/{userId}/tickets")
    public ResponseEntity<?> getTicketsByUser(@PathVariable Long userId, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // So sánh ID trên URL với ID thực tế của Token
        if (!isAdmin && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Lỗi 403: Bạn không có quyền xem vé của người khác!");
        }

        List<Ticket> tickets = ticketService.getTicketsByUserId(userId);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "4. ĐỌC: Lấy toàn bộ vé (Dành cho Admin)")
    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @Operation(summary = "5. ĐỌC: Lấy chi tiết 1 vé theo Ticket ID")
    @GetMapping("/tickets/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ticketService.getTicketById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "6. SỬA: Cập nhật trạng thái vé (PAID/CANCELLED)")
    @PatchMapping("/tickets/{id}/status")
    public ResponseEntity<?> updateTicketStatus(@PathVariable Long id, @RequestParam com.example.movie_sv.model.TicketStatus status) {
        try {
            Ticket updatedTicket = ticketService.updateTicketStatus(id, status);
            return ResponseEntity.ok(updatedTicket);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "7. XÓA: Xóa một vé")
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.deleteTicket(id);
            return ResponseEntity.ok("Xóa vé thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}