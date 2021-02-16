package com.example.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dubbo.common.SysLog;
import com.example.dubbo.entity.SystemLog;
import com.example.dubbo.entity.User;
import com.example.dubbo.mapper.SystemLogMapper;
import com.example.dubbo.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service(interfaceClass = SystemLogService.class)
@Component
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements SystemLogService {

    public static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    @Override
    public List<SystemLog> selectList() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

}
