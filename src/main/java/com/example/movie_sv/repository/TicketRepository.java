package com.example.movie_sv.repository;
import com.example.movie_sv.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId); // Lấy lịch sử mua vé của user
    List<Ticket> findByShowtimeId(Long showtimeId); // Kiểm tra các ghế đã bị đặt trong suất chiếu
}
