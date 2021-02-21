package com.example.dubbo.aspect;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.dubbo.common.SysLog;
import com.example.dubbo.entity.SystemLog;
import com.example.dubbo.jwt.AuthUserInfo;
import com.example.dubbo.util.JwtTokenUtil;
import com.example.dubbo.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * @Author Chang
 * @Description 系统操作日志管理
 * @Date 2021/2/2 15:57
 **/
@Slf4j
@Aspect
@Component
public class SysLogAspect {

    @Reference
    private SystemLogService systemLogService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Pointcut("@annotation(com.example.dubbo.common.SysLog)")
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();

        Object result = point.proceed();

        long time = System.currentTimeMillis() - beginTime;

        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestURI = request.getRequestURI();
        try {
            SysLog syslog = method.getAnnotation(SysLog.class);
            SystemLog systemLog = new SystemLog();
            if (syslog != null) {
                log.info(syslog.value());
                systemLog.setOperation(syslog.value());
            }

            systemLog.setModule(requestURI.substring(1).split("/")[0]);
            systemLog.setIp(InetAddress.getLocalHost().getHostAddress());
            if (requestURI.contains("login") || requestURI.contains("updatePassword") || requestURI.contains("addFyUser")) {
                systemLog.setArgs("参数包含密码接口不存储参数");
            }else {
                // 非登录接口存所有参数，登录接口不能存储密码
                systemLog.setArgs(Arrays.toString(args));
            }

            if (request.getAttribute("userInfo") != null && request.getAttribute("userInfo") instanceof AuthUserInfo) {
                AuthUserInfo userInfo = (AuthUserInfo) request.getAttribute("userInfo");
                systemLog.setUserId(userInfo.getUserId());
            }

            systemLog.setTime((int) time);

            // 类名
            String className = joinPoint.getTarget().getClass().getName();
            // 方法名
            String methodName = signature.getName();
            systemLog.setMethod(className + "." + methodName + "()");

            systemLogService.save(systemLog);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

}
