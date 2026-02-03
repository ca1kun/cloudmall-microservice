package edu.scau.mis.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitOrderConfig {

    // 普通交换机
    public static final String ORDER_EVENT_EXCHANGE = "order.event.exchange";
    // 死信队列 (真正的处理队列)
    public static final String ORDER_RELEASE_QUEUE = "order.release.order.queue";
    // 延时队列 (死信产生地)
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    // 路由键
    public static final String ORDER_CREATE_ROUTING_KEY = "order.create";
    public static final String ORDER_RELEASE_ROUTING_KEY = "order.release";

    // 1. 延时队列：设置 TTL，过期后转发给死信交换机
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", ORDER_EVENT_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_RELEASE_ROUTING_KEY);
        args.put("x-message-ttl", 1800000); // 测试用 1分钟 (生产环境 30分钟 1800000)
        return new Queue(ORDER_DELAY_QUEUE, true, false, false, args);
    }

    // 2. 死信队列：用于接收过期的订单
    @Bean
    public Queue orderReleaseQueue() {
        return new Queue(ORDER_RELEASE_QUEUE, true);
    }

    // 3. 交换机
    @Bean
    public TopicExchange orderEventExchange() {
        return new TopicExchange(ORDER_EVENT_EXCHANGE, true, false);
    }

    // 4. 绑定：延时队列 -> 交换机 (发消息发给这个)
    @Bean
    public Binding orderCreateBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderEventExchange()).with(ORDER_CREATE_ROUTING_KEY);
    }

    // 5. 绑定：死信队列 -> 交换机 (监听器听这个)
    @Bean
    public Binding orderReleaseBinding() {
        return BindingBuilder.bind(orderReleaseQueue()).to(orderEventExchange()).with(ORDER_RELEASE_ROUTING_KEY);
    }
}