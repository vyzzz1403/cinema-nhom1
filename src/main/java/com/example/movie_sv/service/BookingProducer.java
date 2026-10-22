package com.example.movie_sv.service;

import com.example.movie_sv.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingProducer {

    private final RabbitTemplate rabbitTemplate;

    public BookingProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBookingEvent(String message) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            message
        );
        System.out.println("[BOOKING PRODUCER] Đã bắn sự kiện thành công: " + message);
    }
}
