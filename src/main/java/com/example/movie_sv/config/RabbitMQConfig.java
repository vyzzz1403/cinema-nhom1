package com.example.movie_sv.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "bookingQueue";
    public static final String EXCHANGE_NAME = "bookingExchange";
    public static final String ROUTING_KEY = "booking.routingKey";

    @Bean
    public Queue bookingQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue bookingQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(bookingQueue).to(bookingExchange).with(ROUTING_KEY);
    }
}