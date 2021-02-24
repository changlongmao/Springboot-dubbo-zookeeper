package com.example.dubbo.rabbitmq;

import com.example.dubbo.config.RabbitMQConfig;
import com.example.dubbo.entity.User;
import com.example.dubbo.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MsgReceiver
 * @Description:
 * @Author: Chang
 * @Date: 2021/02/23 14:53
 **/
@Slf4j
@Component
public class MsgReceiver {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_A)
    public void processA(Object msg) {
        if (msg instanceof Message) {
            Object obj = ObjectUtils.unserialize(((Message) msg).getBody());
            if (obj instanceof List) {
                List<Map<String, Object>> mapList =(List<Map<String, Object>>) obj;
                log.info("接收处理队列A当中的List： {}", mapList.toString());
            }
            if (obj instanceof Map) {
                Map<String, Object> map =(Map<String, Object>) obj;
                log.info("接收处理队列A当中的Map： {}", map.toString());
            }
            if (obj instanceof User) {
                User user = (User) obj;
                log.info("接收处理队列A当中的User： {}", user);
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_B)
    public void processB(String content) {
        log.info("接收处理队列B当中的消息： {}", content);
    }
}
