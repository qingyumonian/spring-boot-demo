package com.lxf.demo.security.sso.cas;

import com.lxf.demo.config.sso.cas.CasProperties;
import com.lxf.demo.modules.service.ITokenService;
import com.lxf.demo.security.sso.SsoUserSyncService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * CAS认证成功处理器
 * 实现 Spring Security 的 AuthenticationSuccessHandler 接口
 * 处理CAS登录成功后的逻辑：用户同步、Token生成、重定向
 */
@Slf4j
public class CasAuthSuccessHandler implements AuthenticationSuccessHandler {

    private static final String PROVIDER_NAME = "cas";

    private final CasProperties casProperties;
    private final SsoUserSyncService ssoUserSyncService;
    private final ITokenService tokenService;
    private final Long tokenTimeout;
    private final String frontendUrl;

    public CasAuthSuccessHandler(CasProperties casProperties,
                                 SsoUserSyncService ssoUserSyncService,
                                 ITokenService tokenService,
                                 Long tokenTimeout,
                                 String frontendUrl) {
        this.casProperties = casProperties;
        this.ssoUserSyncService = ssoUserSyncService;
        this.tokenService = tokenService;
        this.tokenTimeout = tokenTimeout;
        this.frontendUrl = frontendUrl;
    }

    /**
     * 处理CAS认证成功
     * 从 CasAuthenticationToken 中提取用户信息
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 从 CasAuthenticationToken 中获取用户信息
        CasAuthenticationToken casToken = (CasAuthenticationToken) authentication;
        AttributePrincipal principal = casToken.getAssertion().getPrincipal();

        String username = principal.getName();
        Map<String, Object> attributes = principal.getAttributes();

        // 提取邮箱
        String email = getEmailFromAttributes(attributes);

        log.info("CAS登录成功: username={}, attributes={}", username, attributes);

        // 同步用户到本地数据库
        CustomUserDetails userDetails = ssoUserSyncService.syncUser(
                PROVIDER_NAME,
                username, // CAS用户使用username作为externalId
                username,
                email,
                casProperties.getDefaultRole()
        );

        // 创建token并保存到Redis
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

    /**
     * 从 CAS 属性中提取邮箱
     * 支持 mail 和 email 两种属性名
     */
    private String getEmailFromAttributes(Map<String, Object> attributes) {
        if (attributes == null) {
            return null;
        }

        Object email = attributes.get("mail");
        if (email == null) {
            email = attributes.get("email");
        }

        return email != null ? email.toString() : null;
    }
}