package com.lxf.demo.security.sso.oidc;

import com.lxf.demo.config.sso.SsoProperties;
import com.lxf.demo.modules.service.ITokenService;
import com.lxf.demo.security.sso.SsoUserSyncService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OIDC认证成功处理器
 * 处理Keycloak OIDC登录成功后的逻辑
 */
@Slf4j
@Component
public class OidcAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String PROVIDER_NAME = "keycloak";

    @Resource
    private ITokenService tokenService;

    @Resource
    private SsoUserSyncService ssoUserSyncService;

    @Resource
    private SsoProperties ssoProperties;

    @Value("${auth.token-timeout:3600}")
    private Long tokenTimeout;

    @Value("${auth.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OIDC登录成功");

        // 从认证信息中获取OIDC用户
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        // 提取用户信息
        String sub = oidcUser.getSubject();
        String username = oidcUser.getPreferredUsername();
        String email = oidcUser.getEmail();

        // 如果preferred_username为空，使用sub作为用户名
        if (username == null || username.isEmpty()) {
            username = sub;
        }

        log.info("OIDC用户信息: sub={}, username={}, email={}", sub, username, email);

        // 使用统一SSO用户同步服务同步用户到本地数据库
        String defaultRole = ssoProperties.getKeycloak().getDefaultRole();
        CustomUserDetails userDetails = ssoUserSyncService.syncUser(PROVIDER_NAME, sub, username, email, defaultRole);

        // 创建token并保存到Redis (复用现有TokenService)
        String token = tokenService.saveAccessToken(userDetails);

        // 设置token到Cookie
        Cookie cookie = new Cookie("am_access_token", token);
        cookie.setPath("/");
        cookie.setHttpOnly(false); // 允许前端JS读取
        cookie.setMaxAge(tokenTimeout.intValue());
        response.addCookie(cookie);

        // 设置token到响应头
        response.setHeader("am_access_token", token);

        // 重定向到前端首页
        response.sendRedirect(frontendUrl + "/dashboard");
    }
}
