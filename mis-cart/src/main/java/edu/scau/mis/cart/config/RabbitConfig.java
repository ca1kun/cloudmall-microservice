package edu.scau.mis.cart.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    
    // 定义购物车同步队列
    public static final String CART_SYNC_QUEUE = "cart.sync.queue";

    @Bean
    public Queue cartQueue() {
        return new Queue(CART_SYNC_QUEUE, true);
    }

    // JSON 转换器 (必须有，否则序列化报错)
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}