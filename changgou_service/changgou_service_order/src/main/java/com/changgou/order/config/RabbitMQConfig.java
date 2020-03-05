package com.changgou.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    //添加积分任务交换机
    public static final String EX_BUYING_ADDPOINTUSER = "ex_buying_addpointuser";

    //添加积分消息队列
    public static final String CG_BUYING_ADDPOINT = "cg_buying_addpoint";

    //完成添加积分消息队列
    public static final String CG_BUYING_FINISHADDPOINT = "cg_buying_finishaddpoint";

    //添加积分路由key
    public static final String CG_BUYING_ADDPOINT_KEY = "addpoint";

    //完成添加积分路由key
    public static final String CG_BUYING_FINISHADDPOINT_KEY = "finishaddpoint";

    public static final String ORDER_PAY = "order_pay";

    public static final String ORDER_TACK = "order_tack";

    //死信交换机 与订单过期队列绑定
    public static final String EX_ORDER_TIMEOUT = "exchange.ordertimeout";

    //订单生成队列
    public static final String ORDER_CREATE = "queue.ordercreate";

    //订单过期队列
    public static final String ORDER_TIMEOUT = " queue.ordertimeout";

    @Bean(EX_ORDER_TIMEOUT)
    public Exchange EX_ORDER_TIMEOUT() {
        return ExchangeBuilder.fanoutExchange(EX_ORDER_TIMEOUT).durable(true).build();
    }

    //声明交换机
    @Bean(EX_BUYING_ADDPOINTUSER)
    public Exchange EX_BUYING_ADDPOINTUSER() {
        return ExchangeBuilder.directExchange(EX_BUYING_ADDPOINTUSER).durable(true).build();
    }

    //声明队列
    @Bean(CG_BUYING_ADDPOINT)
    public Queue CG_BUYING_ADDPOINT() {
        Queue queue = new Queue(CG_BUYING_ADDPOINT, true);
        return queue;
    }

    @Bean(CG_BUYING_FINISHADDPOINT)
    public Queue CG_BUYING_FINISHADDPOINT() {
        Queue queue = new Queue(CG_BUYING_FINISHADDPOINT);
        return queue;
    }

    //队列绑定交换机
    @Bean
    public Binding BINDING_CG_BUYING_ADDPOINT(@Qualifier(CG_BUYING_ADDPOINT) Queue queue, @Qualifier(EX_BUYING_ADDPOINTUSER) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CG_BUYING_ADDPOINT_KEY).noargs();
    }

    @Bean
    public Binding BINDING_CG_BUYING_FINISHADDPOINT(@Qualifier(CG_BUYING_FINISHADDPOINT) Queue queue, @Qualifier(EX_BUYING_ADDPOINTUSER) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CG_BUYING_FINISHADDPOINT_KEY).noargs();
    }

    /**
     * 绑定死信队列到死信交换机
     */
    @Bean
    public Binding BINDING_CG_ORDER_TIMEOUT(@Qualifier(ORDER_TIMEOUT) Queue queue, @Qualifier(EX_ORDER_TIMEOUT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    @Bean(ORDER_PAY)
    public Queue queue() {
        return new Queue(ORDER_PAY);
    }

    @Bean(ORDER_TACK)
    public Queue ORDER_TACK() {
        return new Queue(ORDER_TACK);
    }

    @Bean(ORDER_CREATE)
    public Queue ORDER_CREATE() {
        Map<String, Object> arguments = new HashMap<>(2);
        // 绑定该队列到私信交换机
        arguments.put("x-message-ttl", 10000);//10s
        arguments.put("x-dead-letter-exchange", EX_ORDER_TIMEOUT);
        //arguments.put("x-dead-letter-routing-key",dlxRoutingKey);路由
        return new Queue(ORDER_CREATE, true, false, false, arguments);
    }

    @Bean(ORDER_TIMEOUT)
    public Queue ORDER_TIMEOUT() {
        return new Queue(ORDER_TIMEOUT);
    }

}