package com.changgou.pay.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_PAY = "order_pay";

    //订单生成队列
    public static final String ORDER_CREATE = "queue.ordercreate";

    @Bean(ORDER_PAY)
    public Queue ORDER_PAY() {
        return new Queue(ORDER_PAY);
    }

    @Bean(ORDER_CREATE)
    public Queue ORDER_CREATE() {
        return new Queue(ORDER_CREATE);
    }

}