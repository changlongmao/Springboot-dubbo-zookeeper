package com.example.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.dubbo.entity.SystemLog;
import com.example.dubbo.entity.User;
import com.example.dubbo.service.SystemLogService;
import com.example.dubbo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/tranCon")
    @Transactional(rollbackFor = Exception.class)
    public void tranCon() throws Exception{
//        User byId = userService.selectById("00005a4e478a4a14b3c1d9856844c1f2");
//        log.info(byId.toString());

        User user = new User("00005a4e478a4a14b3c1d9856844c1f2");
        user.setUsername("123");
        userService.updateUserById(user);
//        Thread.sleep(10000);
//        log.info("线程1释放");

        SystemLog systemLog = new SystemLog();
        systemLog.setId("1359457619690663937");
        systemLog.setUserName("123");
        systemLogService.updateById(systemLog);
        int i = 1/0;

//        try {
//            int i = 1/0;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @GetMapping("/testMvcc1")
    @Transactional(rollbackFor = Exception.class)
    public void testMvcc1() throws Exception{
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

    @GetMapping("/testMvcc2")
//    @Transactional(rollbackFor = Exception.class)
    public void testMvcc2() throws Exception{
        User user = new User("00005a4e478a4a14b3c1d9856844c1f2");
        user.setUsername("456");
        userService.updateUserByName(user);
//        Thread.sleep(10000);
    }

    public static void main(String[] args) {
        List<Integer> integerList = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            integerList.add(i);
        }
        for (int i = 0; i < integerList.size(); i++) {
            Integer integer = integerList.get(i);
            if (integer > 100 && integer< 1000) {
                integerList.remove(i);
                i--;
            }
        }
//        for (Integer i : integerList) {
//            if (i > 100 && i< 1000) {
//                integerList.remove(i);
//            }
//        }
//        integerList.forEach(i -> {
//            if (i > 100 && i< 1000) {
//                integerList.remove(i);
//            }
//        });
        System.out.println(integerList.size());
    }
}
