package com.example.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dubbo.datasources.DataSourceNames;
import com.example.dubbo.datasources.annotation.DataSource;
import com.example.dubbo.entity.User;
import com.example.dubbo.mapper.UserMapper;
import com.example.dubbo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service(interfaceClass = UserService.class)
@Component
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();


    @Override
    public List<User> selectList() {
        return baseMapper.selectList(new QueryWrapper<>()).stream().limit(10000).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<User> users) {
        baseMapper.batchInsert(users);
    }

    @Override
    @DataSource(DataSourceNames.SECOND)
    public void updateUserById(User user) {
        baseMapper.updateUserById(user);
//        int i = 1/0;
    }

    @Override
    @DataSource(DataSourceNames.FIRST)
    public void updateUserByName(User user) {
        baseMapper.updateUserByName(user);
    }

    @Async("taskExecutor")
    @Override
    public void executeAsync() {
        log.info("start executeAsync");
        try{
            Thread.sleep(5000);
        }catch(Exception e){
            e.printStackTrace();
        }
        log.info("end executeAsync");
    }

    @Override
    public Object getThreadLocal() {
        log.info("service ThreadLocal: {}", threadLocal.get());
        return null;
    }

    @Override
    public User selectById(String id) {
        return baseMapper.getById(id);
    }

    @Override
    public List<Map<String, Object>> getTableName(Map<String, Object> params) {
        return baseMapper.getTableName(params);
    }

    @Override
    public List<Map<String, Object>> optimizeTable(String tableName) {
        baseMapper.optimizeTable(tableName);
        return null;
    }

    @Override
    public List<Map<String, Object>> getDatabaseName(Map<String, Object> params) {
        return baseMapper.getDatabaseName(params);
    }
}
