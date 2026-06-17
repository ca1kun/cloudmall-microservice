package edu.scau.mis.marketing.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String COUPON_EXCHANGE = "coupon.exchange";
    public static final String COUPON_QUEUE = "coupon.queue";
    public static final String COUPON_ROUTING_KEY = "coupon.seckill";

    public static final String COUPON_DLX_EXCHANGE = "coupon.dlx.exchange";
    public static final String COUPON_DLX_QUEUE = "coupon.dlx.queue";
    public static final String COUPON_DLX_ROUTING_KEY = "coupon.dlx";

    @Bean
    public DirectExchange couponExchange() {
        return new DirectExchange(COUPON_EXCHANGE, true, false);
    }

    @Bean
    public Queue couponQueue() {
        return QueueBuilder.durable(COUPON_QUEUE)
                .deadLetterExchange(COUPON_DLX_EXCHANGE)
                .deadLetterRoutingKey(COUPON_DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding couponBinding() {
        return BindingBuilder
                .bind(couponQueue())
                .to(couponExchange())
                .with(COUPON_ROUTING_KEY);
    }

    @Bean
    public DirectExchange couponDlxExchange() {
        return new DirectExchange(COUPON_DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue couponDlxQueue() {
        return QueueBuilder.durable(COUPON_DLX_QUEUE).build();
    }

    @Bean
    public Binding couponDlxBinding() {
        return BindingBuilder
                .bind(couponDlxQueue())
                .to(couponDlxExchange())
                .with(COUPON_DLX_ROUTING_KEY);
    }
}