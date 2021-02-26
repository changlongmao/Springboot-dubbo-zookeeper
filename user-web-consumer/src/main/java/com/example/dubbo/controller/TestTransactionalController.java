package com.example.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.dubbo.entity.SystemLog;
import com.example.dubbo.entity.User;
import com.example.dubbo.service.SystemLogService;
import com.example.dubbo.service.UserService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TestTransactionalController
 * @Description:
 * @Author: Chang
 * @Date: 2021/01/13 16:32
 **/
@Slf4j
@RequestMapping("/testTransactional")
@RestController
public class TestTransactionalController {

    @Reference
    private UserService userService;
    @Reference
    private SystemLogService systemLogService;

    /**
     * @Author Chang
     * @Description 测试Seata分布式事务，支持多个微服务和切换多个数据源
     * @Date 2021/2/26 11:40
     * @Return void
     **/
    @GetMapping("/tranCon")
    @GlobalTransactional
    public void tranCon() throws Exception {
//        User byId = userService.selectById("00005a4e478a4a14b3c1d9856844c1f2");
//        log.info(byId.toString());
        long start = System.currentTimeMillis();

        User user1 = new User();
        user1.setUsername("123");
        user1.setRearName("456");
        userService.updateUserByName(user1);
        User user = new User("0000b5b871914487bad4524c8e245d87");
        user.setUsername("123");
        userService.updateUserById(user);
        SystemLog systemLog = new SystemLog();
        systemLog.setUserId("测试分布式事务3");
        systemLogService.save(systemLog);
//        Thread.sleep(10000);
//        log.info("线程1释放");
        long end = System.currentTimeMillis();

        System.out.println("方法执行时间为：" + (end - start) + "ms");
        int i = 1 / 0;

    }

    @GetMapping("/testMvcc1")
    public void testMvcc1() throws Exception {
//        User user = new User("00005a4e478a4a14b3c1d9856844c1f2");
//        user.setUsername("456");
//        userService.updateUserById(user);
//        User byId = userService.getById("00005a4e478a4a14b3c1d9856844c1f2");
        User byId = userService.selectById("00005a4e478a4a14b3c1d9856844c1f2");
//        log.info(byId.toString());
        Thread.sleep(10000);
        log.info("线程2释放");

//        User user1 = new User("00005a4e478a4a14b3c1d9856844c1f2");
//        user1.setUsername("456");
//        userService.updateUserById(user1);
//        User byId1 = userService.getById("00005a4e478a4a14b3c1d9856844c1f2");
//        log.info(byId1.toString());
    }

}
