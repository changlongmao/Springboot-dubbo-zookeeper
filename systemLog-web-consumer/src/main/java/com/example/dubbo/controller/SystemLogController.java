package com.example.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.dubbo.common.RestResponse;
import com.example.dubbo.common.SysLog;
import com.example.dubbo.entity.SystemLog;
import com.example.dubbo.entity.User;
import com.example.dubbo.util.JwtTokenUtil;
import com.example.dubbo.service.SystemLogService;
import com.example.dubbo.service.UserService;
import com.example.dubbo.util.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/systemLog")
public class SystemLogController {

    private static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    public static int requestNum = 0;

    @Reference
    private UserService userService;
    @Reference
    private SystemLogService systemLogService;
    @Autowired
    private RedissonClient redisson;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JedisUtil jedisUtil;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10,
            30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public static final List<User> userList = Collections.synchronizedList(new ArrayList<>());

    @GetMapping("/selectList")
    public RestResponse selectList() {
        List<SystemLog> systemLogs = systemLogService.selectList();
        return RestResponse.success().put("systemLogs", systemLogs);
    }

    @PostMapping(value = "/save")
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
            log.warn(e.getMessage(), e);
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

    @GetMapping(value = "/myBatchInsert")
    @Transactional(rollbackFor = Exception.class)
    public RestResponse myBatchInsert(HttpServletRequest request) throws Exception {
        long startTime = System.currentTimeMillis();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            user.setUsername("setUsername" + i * 1000);
            user.setPassword("setPassword" + i * 1000);
            user.setRearName("setRearName" + i * 1000);
            users.add(user);
        }
        log.info("批量新增之前：" + users.size());
//        userService.batchInsert(users);
//        log.info("批量新增之后："+users.size());

        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        int j = 0;
        int i = 1000;
        boolean breakFlag = false;
        while (true) {
            if (i >= users.size()) {
                i = users.size();
                breakFlag = true;
            }
            List<User> nextList = users.subList(j, i);
            executor.execute(() -> {
                userService.batchInsert(nextList);
                log.info("批量新增" + nextList.size() + "条");
            });
            if (breakFlag) {
                break;
            }
            j = i;
            i += 1000;
        }
        executor.shutdown();

        log.info("调用awaitTermination之前：" + executor.isTerminated());
        executor.awaitTermination(5, TimeUnit.MINUTES);
        log.info("调用awaitTermination之后：" + executor.isTerminated());

        System.out.println(users.size());
        Long endTime = System.currentTimeMillis();
        System.out.println("myBatchInsert批量插入数据共用时" + (endTime - startTime) + "ms");
        return RestResponse.success();
    }

    @SysLog("测试count")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public RestResponse count(String id) {
        long startTime = System.currentTimeMillis();

        int b = systemLogService.count();
        Long endTime = System.currentTimeMillis();
        System.out.println("查询数据共用时" + (endTime - startTime) + "ms");
        return RestResponse.success().put("count", b);
    }
}
