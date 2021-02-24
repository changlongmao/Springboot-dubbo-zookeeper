package com.example.dubbo.rabbitmq;

import com.example.dubbo.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @ClassName: MsgProducer
 * @Description:
 * @Author: Chang
 * @Date: 2021/02/23 14:51
 **/
@Component
public class MsgProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsgObject(Object obj) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        //把消息放入ROUTING_KEY_A对应的队列当中去，对应的是队列A
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_A, RabbitMQConfig.ROUTING_KEY_A, obj, correlationId);
    }

    public void sendMsgString(String content) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        //把消息放入ROUTING_KEY_B对应的队列当中去，对应的是队列B
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_B, RabbitMQConfig.ROUTING_KEY_B, content, correlationId);
    }

}
