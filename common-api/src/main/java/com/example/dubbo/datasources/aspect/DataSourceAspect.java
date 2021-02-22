package com.example.dubbo.datasources.aspect;

import com.example.dubbo.datasources.DataSourceNames;
import com.example.dubbo.datasources.DynamicDataSource;
import com.example.dubbo.datasources.annotation.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Slf4j
@Aspect
@Component
public class DataSourceAspect implements Ordered {

    @Pointcut("@annotation(com.example.dubbo.datasources.annotation.DataSource)")
    public void dataSourcePointCut() {
    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        DataSource ds = method.getAnnotation(DataSource.class);
        if (ds == null) {
            DynamicDataSource.setDataSource(DataSourceNames.FIRST);
            log.debug("set datasource is " + DataSourceNames.FIRST);
        } else {
            DynamicDataSource.setDataSource(ds.value());
            log.debug("set datasource is " + ds.value());
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSource.clearDataSource();
            log.debug("clean datasource");
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
