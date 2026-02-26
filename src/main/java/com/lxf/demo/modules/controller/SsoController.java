package com.lxf.demo.modules.controller;

import com.lxf.demo.common.result.R;
import com.lxf.demo.modules.dto.SsoCallbackResponse;
import com.lxf.demo.modules.dto.SsoLoginUrlResponse;
import com.lxf.demo.modules.dto.SsoProviderInfoResponse;
import com.lxf.demo.modules.service.ITokenService;
import com.lxf.demo.security.sso.SsoAuthenticationException;
import com.lxf.demo.security.sso.SsoAuthenticationResult;
import com.lxf.demo.security.sso.SsoProvider;
import com.lxf.demo.security.sso.SsoProviderRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 统一SSO控制器
 * 提供前端驱动的SSO登录流程API
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/sso")
public class SsoController {

    private static final String TOKEN_COOKIE_NAME = "am_access_token";
    private static final int COOKIE_MAX_AGE = 7200; // 2小时

    @Resource
    private SsoProviderRegistry providerRegistry;

    @Resource
    private ITokenService tokenService;

    /**
     * 获取所有已启用的SSO提供商
     * GET /api/auth/sso/providers
     */
    @GetMapping("/providers")
    public R<List<SsoProviderInfoResponse>> getProviders() {
        List<SsoProviderInfoResponse> providers = providerRegistry.getEnabledProviders()
                .stream()
                .map(this::toProviderInfo)
                .collect(Collectors.toList());

        return R.ok(providers);
    }

    /**
     * 获取指定提供商的登录URL
     * GET /api/auth/sso/{provider}/login-url
     *
     * @param provider    提供商名称（cas/keycloak）
     * @param redirectUri 登录成功后重定向的前端页面URL
     * @param state       可选的状态参数
     */
    @GetMapping("/{provider}/login-url")
    public R<SsoLoginUrlResponse> getLoginUrl(
            HttpServletRequest request,
            @PathVariable String provider,
            @RequestParam(required = false) String redirectUri,
            @RequestParam(required = false) String state) {

        SsoProvider ssoProvider = providerRegistry.getProviderOrThrow(provider);

        if (!ssoProvider.isEnabled()) {
            return R.fail("SSO提供商未启用: " + provider);
        }
        StringBuilder stringBuilder = new StringBuilder();
        StringBuffer stringBuffer = new StringBuffer();
        // 生成state参数（如果未提供）
        String actualState = StringUtils.hasText(state) ? state : UUID.randomUUID().toString();

        String loginUrl = ssoProvider.getLoginUrl(redirectUri, actualState);

        // 如果返回的是相对路径，拼接成完整URL（基于后端地址）
        if (loginUrl.startsWith("/")) {
            String baseUrl = getBaseUrl(request);
            loginUrl = baseUrl + loginUrl;
        }

        SsoLoginUrlResponse response = SsoLoginUrlResponse.builder()
                .provider(provider)
                .loginUrl(loginUrl)
                .state(actualState)
                .build();

        return R.ok(response);
    }

    /**
     * 获取请求的基础URL
     */
    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if (("http".equals(scheme) && serverPort != 80) ||
                ("https".equals(scheme) && serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        return url.toString();
    }




    /**
     * 获取登出URL
     * GET /api/auth/sso/{provider}/logout-url
     */
    @GetMapping("/{provider}/logout-url")
    public R<Map<String, String>> getLogoutUrl(
            @PathVariable String provider,
            @RequestParam(required = false) String postLogoutRedirectUri) {

        SsoProvider ssoProvider = providerRegistry.getProviderOrThrow(provider);

        String logoutUrl = ssoProvider.getLogoutUrl(postLogoutRedirectUri);

        Map<String, String> result = new HashMap<>();
        result.put("provider", provider);
        result.put("logoutUrl", logoutUrl);

        return R.ok(result);
    }

    /**
     * 执行登出
     * POST /api/auth/sso/{provider}/logout
     */
    @PostMapping("/{provider}/logout")
    public R<Map<String, String>> performLogout(
            @PathVariable String provider,
            @RequestParam(required = false) String postLogoutRedirectUri,
            @RequestHeader(value = "am_access_token", required = false) String headerToken,
            @CookieValue(value = TOKEN_COOKIE_NAME, required = false) String cookieToken,
            HttpServletResponse response) {

        // 移除本地Token
        String token = StringUtils.hasText(headerToken) ? headerToken : cookieToken;
        if (StringUtils.hasText(token)) {
            tokenService.removeAccessToken(token);
        }

        // 清除Cookie
        clearTokenCookie(response);

        // 获取SSO登出URL
        SsoProvider ssoProvider = providerRegistry.getProviderOrThrow(provider);
        String logoutUrl = ssoProvider.getLogoutUrl(postLogoutRedirectUri);

        Map<String, String> result = new HashMap<>();
        result.put("provider", provider);
        result.put("logoutUrl", logoutUrl);
        result.put("message", "本地登出成功，请访问logoutUrl完成SSO登出");

        return R.ok(result);
    }

    /**
     * 转换为提供商信息响应
     */
    private SsoProviderInfoResponse toProviderInfo(SsoProvider provider) {
        Map<String, Object> info = new HashMap<>();
        info.put("type", provider.getProviderName());

        return SsoProviderInfoResponse.builder()
                .name(provider.getProviderName())
                .enabled(provider.isEnabled())
                .info(info)
                .build();
    }



    /**
     * 清除Token Cookie
     */
    private void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
