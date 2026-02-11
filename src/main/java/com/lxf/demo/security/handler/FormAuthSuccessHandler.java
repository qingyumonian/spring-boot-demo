package com.lxf.demo.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxf.demo.common.result.R;
import com.lxf.demo.modules.dto.LoginResponse;
import com.lxf.demo.modules.service.IRoleService;
import com.lxf.demo.modules.service.ITokenService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证成功后相应地址，同时把token返回
 * @author lixuefei
 * @date 2026/02/10 16:42
 **/
@Slf4j
@Component
public class FormAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Resource
    private ITokenService tokenService;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${auth.token-timeout:3600}")
    private Long tokenTimeout;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("表单登陆成功");

        // 从认证信息中获取用户详情
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 创建token并保存到Redis
        String token = tokenService.saveAccessToken(userDetails);

        // 构建登录响应
        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
//                .role(userDetails.getRole())
                .expiresIn(tokenTimeout)
                .build();

        // 设置token到Cookie
        Cookie cookie = new Cookie("am_access_token", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(tokenTimeout.intValue());
        response.addCookie(cookie);

        // 设置token到响应头
        response.setHeader("am_access_token", token);

        // 写入JSON响应
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(R.ok(loginResponse)));
    }
}
