package com.example.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.dubbo.entity.User;
import com.example.dubbo.rabbitmq.MsgProducer;
import com.example.dubbo.service.SystemLogService;
import com.example.dubbo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: TestTransactionalController
 * @Description:
 * @Author: Chang
 * @Date: 2021/01/13 16:32
 **/
@Slf4j
@RequestMapping("/testRabbitMQ")
@RestController
public class TestRabbitMQController {

    @Reference
    private UserService userService;
    @Reference
    private SystemLogService systemLogService;
    @Autowired
    private MsgProducer msgProducer;


    @GetMapping("/msgProducer")
    public void msgProducer() throws Exception {
        // 生产对象传输
        User user1 = new User("给队列A发送消息1");
        msgProducer.sendMsgObject(user1);
        User user2 = new User("给队列A发送消息2");
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        // 生产LIst对象传输
        msgProducer.sendMsgObject(users);
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("A", "消息1");
        hashMap.put("B", "消息2");
        // 生产Map对象传输
        msgProducer.sendMsgObject(hashMap);
        // 生产文本消息传输
        msgProducer.sendMsgString("给队列B发送消息");
    }

}
