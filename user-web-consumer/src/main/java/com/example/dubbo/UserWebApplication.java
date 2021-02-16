package com.example.dubbo;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author LongFei
 */
@EnableDubbo
@SpringBootApplication
public class UserWebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(UserWebApplication.class, args);
    }

}
