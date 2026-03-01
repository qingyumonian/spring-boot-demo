package com.lxf.demo.common.exception;

import com.lxf.demo.common.result.R;
import com.lxf.demo.utils.TracingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBusinessException(BusinessException e) {
        TracingUtil.recordExceptionAsync("BusinessException", e, getRequestInfo());
        return R.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Void> handleAuthenticationException(AuthenticationException e) {
        TracingUtil.recordExceptionAsync("AuthenticationException", e, getRequestInfo());
        return R.fail(401, "认证失败: " + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Void> handleAccessDeniedException(AccessDeniedException e) {
        TracingUtil.recordExceptionAsync("AccessDeniedException", e, getRequestInfo());
        return R.fail(403, "无权限访问");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e) {
        TracingUtil.recordExceptionAsync("UnhandledException", e, getRequestInfo());
        log.error("服务器错误:" + e.getMessage());
        return R.fail(500, "服务器错误: " + e.getMessage());
    }

    private String getRequestInfo() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                return request.getMethod() + " " + request.getRequestURI();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
