package com.example.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.dubbo.common.RestResponse;
import com.example.dubbo.common.SysLog;
import com.example.dubbo.datasources.DataSourceNames;
import com.example.dubbo.datasources.annotation.DataSource;
import com.example.dubbo.entity.User;
import com.example.dubbo.jwt.AuthUser;
import com.example.dubbo.jwt.AuthUserInfo;
import com.example.dubbo.jwt.Authorization;
import com.example.dubbo.service.SystemLogService;
import com.example.dubbo.service.UserService;
import com.example.dubbo.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();


    @Reference
    private UserService userService;
    @Reference
    private SystemLogService systemLogService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @GetMapping("/selectList")
    public RestResponse selectList() {
        List<User> users = userService.selectList();
        return RestResponse.success().put("users", users);
    }


    @RequestMapping(value = "/mybatisPlusBatchInsert", method = RequestMethod.POST)
    public RestResponse mybatisPlusBatchInsert(HttpServletRequest request) {
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

        userService.saveBatch(users);
        Long endTime = System.currentTimeMillis();
        System.out.println("mybatisPlusBatchInsert批量插入数据共用时" + (endTime - startTime) + "ms");
        return RestResponse.success().put("users", users);
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

        log.info("批量新增之后："+users.size());
        Long endTime = System.currentTimeMillis();
        System.out.println("myBatchInsert批量插入数据共用时" + (endTime - startTime) + "ms");
        return RestResponse.success();
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @DataSource(DataSourceNames.SECOND)
    public RestResponse count(String id) {
        long startTime = System.currentTimeMillis();

        int b = userService.count();
        Long endTime = System.currentTimeMillis();
        System.out.println("查询数据共用时" + (endTime - startTime) + "ms");
        return RestResponse.success().put("count", b);
    }


    @SysLog("获取token")
    @RequestMapping(value = "/getToken", method = RequestMethod.GET)
    public RestResponse getToken(@RequestParam Map<String, Object> params) throws Exception {
        Thread.sleep(1000);
        String token = jwtTokenUtil.generateToken("1154218600098865154", 1);

        return RestResponse.success().put("token", token);
    }

    @SysLog("测试AspectAdvice")
    @Authorization
    @PostMapping(value = "/testAspectAdvice")
    public RestResponse testAspectAdvice(@AuthUser AuthUserInfo userInfo, @RequestBody Map<String, Object> params) throws Exception {
        return RestResponse.success().put("token", "测试成功");
    }


    @Authorization
    @RequestMapping(value = "/testAuthorization", method = RequestMethod.GET)
    public RestResponse testAuthorization(@AuthUser AuthUserInfo userInfo, @RequestParam String user) {
        System.out.println(userInfo);
        System.out.println(user);
        return RestResponse.success();
    }
}
