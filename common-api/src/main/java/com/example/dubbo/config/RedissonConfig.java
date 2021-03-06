package com.example.dubbo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout}")
    private String strTimeout;
    @Value("${spring.redis.database}")
    private Integer database;
    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private String strMaxWaitMillis;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private Integer maxIdle;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Bean
    public RedissonClient getRedisson(){
        Integer timeout = Integer.parseInt(strTimeout.replace("ms", ""));
        Config config = new Config();
        config.useSingleServer()
                .setPingConnectionInterval(60)
                .setKeepAlive(true)
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password)
                .setTimeout(timeout)
                .setDatabase(database);
        return Redisson.create(config);
    }
}
