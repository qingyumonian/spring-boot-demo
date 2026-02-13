package com.lxf.demo.security.sso.cas;

import com.lxf.demo.config.sso.cas.CasProperties;
import com.lxf.demo.modules.service.ITokenService;
import com.lxf.demo.security.sso.SsoUserSyncService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CAS认证成功处理器
 * 处理CAS登录成功后的逻辑：用户同步、Token生成、重定向
 */
@Slf4j
public class CasAuthSuccessHandler {

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
     */
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        CasUserInfo userInfo) throws IOException {
        log.info("CAS登录成功: username={}", userInfo.getUsername());

        String username = userInfo.getUsername();
        String email = userInfo.getEmail();

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
}
