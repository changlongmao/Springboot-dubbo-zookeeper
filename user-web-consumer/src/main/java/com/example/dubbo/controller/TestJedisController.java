package com.example.dubbo.controller;

import com.example.dubbo.common.RestResponse;
import com.example.dubbo.entity.User;
import com.example.dubbo.util.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


/**
 * @ClassName: TestController
 * @Description:
 * @Author: Chang
 * @Date: 2020/12/28 14:44
 **/

@Slf4j
@RestController
@RequestMapping("/testJedis")
public class TestJedisController {

    @Autowired
    private JedisUtil jedisUtil;

    @GetMapping("testJedisObject")
    public RestResponse testJedisObject() throws Exception {

        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        User user = new User("111");
        map.put("user", user);
        users.add(map);

        jedisUtil.setObject("a", users, 1);
        log.info("剩余时间" + jedisUtil.ttl("a"));

        List<Map<String, Object>> a = (List<Map<String, Object>>) jedisUtil.getObject("a");
        return RestResponse.success().put("a", a.toString());
    }

    @GetMapping("testJedisList")
    public RestResponse testJedisList() throws Exception {

        List<String> strings = new ArrayList<>();
        strings.add("111");
        jedisUtil.setList("strings", strings, 1);
        System.out.println(jedisUtil.getList("strings"));
        jedisUtil.listAdd("strings", "222", "333");
        System.out.println(jedisUtil.getList("strings"));
        System.out.println(jedisUtil.getKeysByPrefix(""));
        return RestResponse.success();
    }

    @GetMapping("testExpireTime")
    public RestResponse testExpireTime() throws Exception {

//        jedisUtil.set("str", "aaa", 0);
        jedisUtil.set("str", "aaa", 1);
//        jedisUtil.set("str", "aaa", -1);
        Thread.sleep(1000);
        System.out.println(jedisUtil.ttl("str"));
        System.out.println(jedisUtil.get("str"));

        System.out.println(jedisUtil.getKeysByPrefix(""));
        return RestResponse.success();
    }

}
