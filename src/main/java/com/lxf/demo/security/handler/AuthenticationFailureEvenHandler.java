package com.lxf.demo.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lixuefei
 * @date 2026/02/10 16:54
 **/
@Slf4j
@Component
public  class AuthenticationFailureEvenHandler implements ApplicationListener<AbstractAuthenticationFailureEvent> {


    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();

        AuthenticationException authenticationException = event.getException();
        Authentication authentication = (Authentication) event.getSource();

        handle(authenticationException, authentication, request, response);
    }

    private void handle(AuthenticationException authenticationException, Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        //记录登陆失败
        log.error("AuthenticationFailureEvenHandler 登陆失败");
    }

}
