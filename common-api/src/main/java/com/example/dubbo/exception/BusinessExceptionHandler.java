package com.example.dubbo.exception;

import com.example.dubbo.common.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @Author Chang
 * @Description 自定义全局异常拦截器，对异常统一处理
 * @Date 2020/10/22 16:17
 **/
@RestControllerAdvice
public class BusinessExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BusinessExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public RestResponse handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return RestResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public RestResponse handlerNoFoundException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return RestResponse.error(HttpStatus.NOT_FOUND.value(), "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public RestResponse handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return RestResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "数据库中已存在该记录");
    }

    @ExceptionHandler(NumberFormatException.class)
    public RestResponse handleNumberFormatException(NumberFormatException e) {
        log.error(e.getMessage(), e);
        return RestResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "数字解析错误，参数需数字得文字");
    }

    @ExceptionHandler(Exception.class)
    public RestResponse handleException(Exception e) {
        log.error(e.getMessage(), e);
        return RestResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未知异常，请联系管理员");
    }
}
