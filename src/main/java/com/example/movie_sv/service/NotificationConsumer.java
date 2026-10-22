package com.example.movie_sv.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    // Lắng nghe thông điệp từ Queue đã được cấu hình trong RabbitMQConfig
    // (Hãy đảm bảo tên queue khớp với queue bạn khai báo, ví dụ: "booking.queue" hoặc "bookingQueue")
    @RabbitListener(queues = "bookingQueue")
    public void handleBookingNotification(String bookingEventMessage) {
        System.out.println("--------------------------------------------------");
        System.out.println("[NOTIFICATION SERVICE] Nhận được sự kiện đặt vé mới!");
        System.out.println("Nội dung thông điệp: " + bookingEventMessage);
        System.out.println("-> Đang tiến hành gửi email/SMS thông báo cho khách hàng...");
        System.out.println("--------------------------------------------------");
    }
}
