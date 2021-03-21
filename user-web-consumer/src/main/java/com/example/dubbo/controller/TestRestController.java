package com.example.dubbo.controller;

import com.example.dubbo.common.RestResponse;
import com.example.dubbo.entity.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: TestRestController
 * @Description:
 * @Author: Chang
 * @Date: 2021/03/17 18:25
 **/
@Slf4j
@RequestMapping("/testRest")
@RestController
public class TestRestController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String URL = "http://localhost:9001";

    @GetMapping("/selectList")
    public RestResponse selectList() {
        RestResponse response = restTemplate.getForObject(URL + "/systemLog/selectList", RestResponse.class);
        log.info(response.toString());
        ResponseEntity<RestResponse> responseEntity1 = restTemplate.getForEntity(URL + "/systemLog/selectList", RestResponse.class);
        HttpStatus statusCode = responseEntity1.getStatusCode();
        HttpHeaders header = responseEntity1.getHeaders();
        RestResponse body = responseEntity1.getBody();
        log.info("get RestResponse:"+ body);
        log.info("get statusCode:"+statusCode);
        log.info("get header:"+header);
        return response;
    }


    @PostMapping(value = "/save")
    public RestResponse save(@RequestBody Map<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        params.put("code", "1234");
        HttpEntity<Map<String, Object>> formEntity = new HttpEntity<>(params, headers);
        //1、通过postForObject()调用
        RestResponse testEntity1 = this.restTemplate.postForObject(URL + "/systemLog/save",formEntity, RestResponse.class);
        System.out.println("post testEntity1:"+testEntity1);

        //2、通过postForEntity()调用
        ResponseEntity<RestResponse> responseEntity1 = this.restTemplate.postForEntity(URL + "/systemLog/save", formEntity,RestResponse.class);
        HttpStatus statusCode = responseEntity1.getStatusCode();
        HttpHeaders header = responseEntity1.getHeaders();
        RestResponse testEntity2 = responseEntity1.getBody();
        System.out.println("post testEntity2:"+testEntity2);
        System.out.println("post statusCode:"+statusCode);
        System.out.println("post header:"+header);
        return testEntity1;
    }
}
