package com.example.dubbo;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.example.dubbo.datasources.DynamicDataSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

/**
 * @author LongFei
 */
@EnableDubbo
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Import({DynamicDataSourceConfig.class})
public class SystemLogWebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SystemLogWebApplication.class, args);
    }

}
