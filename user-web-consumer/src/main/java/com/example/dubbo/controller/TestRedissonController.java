package com.example.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.dubbo.common.RestResponse;
import com.example.dubbo.entity.User;
import com.example.dubbo.service.SystemLogService;
import com.example.dubbo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: TestTransactionalController
 * @Description:
 * @Author: Chang
 * @Date: 2021/01/13 16:32
 **/
@Slf4j
@RequestMapping("/testRedisson")
@RestController
public class TestRedissonController {

    @Reference
    private UserService userService;
    @Reference
    private SystemLogService systemLogService;
    @Autowired
    private RedissonClient redisson;

    public static int requestNum = 0;
    public static final List<User> userList = Collections.synchronizedList(new ArrayList<>());

    /**
     * @Param: params
     * @Author Chang
     * @Description 测试redissonLock分布式锁
     * @Date 2021/2/26 11:37
     * @Return com.example.dubbo.common.RestResponse
     **/
    @GetMapping(value = "/save")
    @Transactional(rollbackFor = Exception.class)
    public RestResponse save(@RequestBody Map<String, Object> params) {
        log.info("code:{}", params.get("code"));
        RLock lock = redisson.getLock("save" + params.get("code"));
//        if (lock.isLocked()) {
//            log.info("未获取到锁，请求失败");
//            return RestResponse.error("点击过快，请勿重复请求");
//        }
        boolean tryLock = false;
        long startTime = System.currentTimeMillis();
        try {
//            lock.lock(60L, TimeUnit.SECONDS);
            // waitTime为若没获取到锁的等待时间，超时则放弃获取锁返回false,leaseTime若获取锁超过指定的时间还没释放则自动释放
            if (tryLock = lock.tryLock(1, 60, TimeUnit.SECONDS)) {
                long waitLockTime = System.currentTimeMillis();
                requestNum++;
                log.info("请求{}获取到锁，请求成功", requestNum);
                log.info("请求{}等待获取锁用时：{}ms", requestNum, waitLockTime - startTime);
                for (int i = 0; i < 10; i++) {
                    User user = new User();
                    user.setId(UUID.randomUUID().toString().replace("-", ""));
                    user.setUsername("longMao" + requestNum);
                    user.setPassword("123");
                    user.setRearName("龙猫" + requestNum);
                    user.setCreateTime(new Date());
                    userList.add(user);
                }
                Thread.sleep(300);
                return RestResponse.success("操作成功");
            }else {
                log.warn("没有获取到锁");
                return RestResponse.error("请求失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return RestResponse.error("操作异常");
        } finally {
            if (tryLock) {
                log.info("请求{}释放锁", requestNum);
                log.info("请求{}时userList元素个数为：{}个", requestNum, userList.size());
                long requestTime = System.currentTimeMillis();
                log.info("请求{}接口共用时：{}ms", requestNum, requestTime - startTime);
                lock.unlock();
            }
        }

    }


}
