package com.example.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.dubbo.common.RestResponse;
import com.example.dubbo.entity.User;
import com.example.dubbo.jwt.AuthUser;
import com.example.dubbo.jwt.AuthUserInfo;
import com.example.dubbo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName: TestThreadController
 * @Description:
 * @Author: Chang
 * @Date: 2021/01/07 09:20
 **/
@Slf4j
@RestController
@RequestMapping("/testThread")
public class TestThreadController {

    public static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();
    public static final ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10,
            30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;
    @Reference
    private UserService userService;
    private static Integer index;

    @GetMapping("/testThreadLocal")
    public RestResponse testThreadLocal() throws Exception {

        index = 1;
        log.info(index + "");
        threadLocal.set(123);
        threadLocal.set("aaa");
        Thread.sleep(5000);
        log.info("threadLocal" + threadLocal.get());
        return RestResponse.success().put("threadLocal", threadLocal.get());
    }

    @GetMapping("/testThreadLocalGet")
    public RestResponse testThreadLocalGet() throws Exception {

        index = 2;
        log.info(index + "");
        threadLocal.set("bbb");
        threadLocal.set(456);
        Thread.sleep(5000);
        log.info("threadLocal" + threadLocal.get());
        return RestResponse.success().put("threadLocal", threadLocal.get());
    }

    @GetMapping("/testLogLevel")
    public RestResponse testLogLevel() throws Exception {

        System.out.println("开始");

        new Thread(() -> {
            while (true) {
                System.out.println("i");
            }
        });
        System.out.println("结束");

        return RestResponse.success();
    }


    @GetMapping(value = "/testList")
    public RestResponse testList() throws Exception {
        long startTime = System.currentTimeMillis();
//        List<User> users = new ArrayList<>();
//        List<User> users = new CopyOnWriteArrayList<>();
        List<User> users = new Vector<>();
//        List<User> users = Collections.synchronizedList(new ArrayList<>());
//        for (int i = 0; i < 500000; i++) {
//            User user = new User();
//            user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
//            user.setPassword("setPassword" + i * 1000);
//            user.setUsername("setUsername" + i * 1000);
//            user.setRearName("setRearName" + i * 1000);
//            users.add(user);
//        }

        for (int j = 0; j < 10; j++) {
            executor.execute(() -> {
                for (int i = 0; i < 50000; i++) {
                    User user = new User();
                    user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    users.add(user);
                }
            });
        }
        executor.shutdown();

        // executor.awaitTermination()作用：等待所有线程执行完毕
        log.info("调用awaitTermination之前：" + executor.isTerminated());
        executor.awaitTermination(5, TimeUnit.MINUTES);
        log.info("调用awaitTermination之后：" + executor.isTerminated());
        log.info("循环add数据: " + users.size() + "条");

        Long endTime = System.currentTimeMillis();
        System.out.println("循环add数据共用时" + (endTime - startTime) + "ms");
        return RestResponse.success();
    }

    @GetMapping(value = "/testMap")
    public RestResponse testMap() throws Exception {
        long startTime = System.currentTimeMillis();
//        Map<String, User> users = new HashMap<>();
//        Map<String, User> users = new Hashtable<>();
        Map<String, User> users = Collections.synchronizedMap(new HashMap<>());
//        Map<String, User> users = new ConcurrentHashMap<>();
//        for (int i = 0; i < 3000000; i++) {
//            User user = new User();
//            user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
//            user.setPassword("setPassword" + i * 1000);
//            user.setUsername("setUsername" + i * 1000);
//            user.setRearName("setRearName" + i * 1000);
//            users.put(user.getId(), user);
//        }

        for (int j = 0; j < 10; j++) {
            executor.execute(() -> {
                for (int i = 0; i < 50000; i++) {
                    User user = new User();
                    user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    users.put(user.getId(), user);
                }
            });
        }
        executor.shutdown();

        log.info("调用awaitTermination之前：" + executor.isTerminated());
        executor.awaitTermination(10, TimeUnit.MINUTES);
        log.info("调用awaitTermination之后：" + executor.isTerminated());
        log.info("循环put数据: " + users.size() + "条");

        Long endTime = System.currentTimeMillis();
        System.out.println("循环put数据共用时" + (endTime - startTime) + "ms");
        return RestResponse.success();
    }

    @GetMapping(value = "/testString")
    public RestResponse testString() throws Exception {
        long start = System.currentTimeMillis();

        StringBuffer sb = new StringBuffer();
//        String sb = "";
//        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                for (int j = 0; j < 1000000; j++) {
                    sb.append("啊");
                }
            });
        }

        executor.shutdown();

        log.info("调用awaitTermination之前：" + executor.isTerminated());
        executor.awaitTermination(5, TimeUnit.MINUTES);
        log.info("调用awaitTermination之后：" + executor.isTerminated());

        long end = System.currentTimeMillis();
        log.info("字符串长度为: {}", sb.length());
        System.out.println("拼接字符串共用时" + (end - start) + "ms");
        return RestResponse.success();
    }

    @RequestMapping(value = "/executeAsync", method = RequestMethod.GET)
    public RestResponse executeAsync(@AuthUser AuthUserInfo userInfo) throws Exception {
        userService.executeAsync();

        return RestResponse.success();
    }

}
