package com.lxf.demo.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lixuefei
 * @date 2026/02/10 16:56
 **/
@Slf4j
@Component
public class AuthenticationSuccessEvenHandler implements ApplicationListener<AuthenticationSuccessEvent> {
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();

        Authentication authentication = (Authentication) event.getSource();
        handle(authentication, request, response);
    }

    private void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        //todo 记录登陆成功
        log.info("AuthenticationSuccessEvenHandler 登陆成功");
    }
}
