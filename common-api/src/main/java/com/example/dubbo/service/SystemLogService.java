package com.example.dubbo.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dubbo.entity.SystemLog;
import com.example.dubbo.entity.User;

import java.util.List;
import java.util.Map;

public interface SystemLogService extends IService<SystemLog> {

    List<SystemLog> selectList();
}
