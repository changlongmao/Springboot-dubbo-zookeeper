package com.example.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dubbo.common.RestResponse;
import com.example.dubbo.common.SysLog;
import com.example.dubbo.datasources.DataSourceNames;
import com.example.dubbo.datasources.annotation.DataSource;
import com.example.dubbo.entity.User;
import com.example.dubbo.jwt.AuthUser;
import com.example.dubbo.jwt.AuthUserInfo;
import com.example.dubbo.jwt.Authorization;
import com.example.dubbo.util.JwtTokenUtil;
import com.example.dubbo.service.SystemLogService;
import com.example.dubbo.service.UserService;
import com.example.dubbo.util.JedisUtil;
import com.example.dubbo.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

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
        List<User> users = userService.selectList();
        return RestResponse.success().put("users", users);
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
//        userService.batchInsert(users);
//        User user = new User();
//        user.setId("00002b33bbd14cf187e7c769238e452b");
//        user.setUsername("myBatchInsert123");
//        user.setRearName("myBatchInsert456");
//        userService.updateUserById(user);
//        userService.getById("00001d7567a64e358fc9903403f025f8");
//        userService.updateUserByName(user);
//        Thread.sleep(30000);

        System.out.println(users.size());
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

    @RequestMapping(value = "/selectOne", method = RequestMethod.GET)
    public RestResponse selectOne(String id) {
        long startTime = System.currentTimeMillis();

        User user = userService.getById(id);
        Long endTime = System.currentTimeMillis();
        System.out.println("查询数据共用时" + (endTime - startTime) + "ms");
        return RestResponse.success().put("User", user);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public RestResponse delete() {
        long startTime = System.currentTimeMillis();

        userService.remove(new QueryWrapper<User>().eq("PASSWORD", "123"));
        Long endTime = System.currentTimeMillis();
        System.out.println("查询数据共用时" + (endTime - startTime) + "ms");
        return RestResponse.success();
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

    @RequestMapping(value = "/testJedis", method = RequestMethod.GET)
    public RestResponse testJedis(@RequestParam Map<String, Object> params) throws Exception {
//        jedisUtil.del("testJedis");

        String testJedis = jedisUtil.get("testJedis");
        if (StringUtils.isNotBlank(testJedis)) {
            log.info("从redis取数据");
            return RestResponse.success().put("testJedis", testJedis);
        }
        log.info("从后台取数据");
        String a = "aaa";
        jedisUtil.set("testJedis", "aaa", 60);

        return RestResponse.success().put("testJedis", a);
    }

    @RequestMapping(value = "/executeAsync", method = RequestMethod.GET)
    public RestResponse executeAsync(@AuthUser AuthUserInfo userInfo) throws Exception {
        userService.executeAsync();

        return RestResponse.success();
    }

    @RequestMapping(value = "/getBase64", method = RequestMethod.POST)
    public RestResponse getBase64(@RequestParam MultipartFile file) throws IOException {
        File tmp = File.createTempFile("tem", null);
        file.transferTo(tmp);
        tmp.deleteOnExit();
        FileInputStream inputFile = new FileInputStream(tmp);
        byte[] buffer = new byte[(int) tmp.length()];
        inputFile.read(buffer);
        inputFile.close();
        String base64 = new BASE64Encoder().encode(buffer);
//        BASE64Encoder base64Encoder =new BASE64Encoder();
//        String base64EncoderImg = file.getOriginalFilename()+","+ base64Encoder.encode(file.getBytes());

        String s = base64.replaceAll("[\\s*\t\n\r]", "");
        return RestResponse.success().put("base64", s);
    }


    @Authorization
    @RequestMapping(value = "/testAuthorization", method = RequestMethod.GET)
    public RestResponse testAuthorization(@AuthUser AuthUserInfo userInfo, @RequestParam String user) {
        System.out.println(userInfo);
        System.out.println(user);
        return RestResponse.success();
    }
}
