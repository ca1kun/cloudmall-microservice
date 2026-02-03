package edu.scau.mis.marketing.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter; // 👈 导入这个
import org.springframework.amqp.support.converter.MessageConverter; // 👈 导入这个
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String COUPON_QUEUE = "coupon.queue";

    @Bean
    public Queue couponQueue() {
        return new Queue(COUPON_QUEUE, true);
    }

    // 👇👇👇 【核心修复】添加这个 Bean 👇👇👇
    // 这会让 RabbitMQ 发送和接收时都使用 JSON 格式，而不是 Java 二进制
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}