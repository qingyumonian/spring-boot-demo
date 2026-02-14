package com.lxf.demo.security.sso.cas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxf.demo.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * CAS认证失败处理器
 * 实现 Spring Security 的 AuthenticationFailureHandler 接口
 * 处理CAS登录失败的情况
 */
@Slf4j
public class CasAuthFailHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理CAS认证失败
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.error("CAS认证失败: {}", exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        R<Void> result = R.fail(401, "CAS认证失败: " + exception.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}