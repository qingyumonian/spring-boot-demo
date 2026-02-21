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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
            @PathVariable String provider,
            @RequestParam(required = false) String redirectUri,
            @RequestParam(required = false) String state) {

        SsoProvider ssoProvider = providerRegistry.getProviderOrThrow(provider);

        if (!ssoProvider.isEnabled()) {
            return R.fail("SSO提供商未启用: " + provider);
        }

        // 生成state参数（如果未提供）
        String actualState = StringUtils.hasText(state) ? state : UUID.randomUUID().toString();

        String loginUrl = ssoProvider.getLoginUrl(redirectUri, actualState);

        SsoLoginUrlResponse response = SsoLoginUrlResponse.builder()
                .provider(provider)
                .loginUrl(loginUrl)
                .state(actualState)
                .build();

        return R.ok(response);
    }

    /**
     * SSO回调处理（重定向流程）
     * GET /api/auth/sso/{provider}/callback
     *
     * 处理SSO服务器的回调，验证后重定向到前端页面
     */
    @GetMapping("/{provider}/callback")
    public void handleCallback(
            @PathVariable String provider,
            @RequestParam(required = false) String redirectUri,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        try {
            SsoProvider ssoProvider = providerRegistry.getProviderOrThrow(provider);

            if (!ssoProvider.isEnabled()) {
                redirectWithError(response, redirectUri, "PROVIDER_DISABLED", "SSO提供商未启用");
                return;
            }

            SsoAuthenticationResult result = ssoProvider.handleCallback(request);

            // 设置Token Cookie
            setTokenCookie(response, result.getAccessToken());

            // 重定向到前端页面
            String targetUrl = buildRedirectUrl(redirectUri, result);
            log.info("SSO认证成功，重定向到: {}", targetUrl);
            response.sendRedirect(targetUrl);

        } catch (SsoAuthenticationException e) {
            log.error("SSO认证失败: provider={}, error={}, message={}",
                    e.getProvider(), e.getErrorCode(), e.getMessage());
            redirectWithError(response, redirectUri, e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("SSO认证异常: provider={}", provider, e);
            redirectWithError(response, redirectUri, "AUTH_ERROR", "认证处理异常");
        }
    }

    /**
     * SSO回调处理（JSON响应）
     * POST /api/auth/sso/{provider}/callback/json
     *
     * 处理SSO回调，返回JSON响应而非重定向
     * 适用于SPA应用的异步处理场景
     */
    @PostMapping("/{provider}/callback/json")
    public R<SsoCallbackResponse> handleCallbackJson(
            @PathVariable String provider,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            SsoProvider ssoProvider = providerRegistry.getProviderOrThrow(provider);

            if (!ssoProvider.isEnabled()) {
                return R.fail("SSO提供商未启用: " + provider);
            }

            SsoAuthenticationResult result = ssoProvider.handleCallback(request);

            // 设置Token Cookie
            setTokenCookie(response, result.getAccessToken());

            SsoCallbackResponse callbackResponse = SsoCallbackResponse.builder()
                    .token(result.getAccessToken())
                    .username(result.getUsername())
                    .expiresIn(result.getExpiresIn())
                    .provider(provider)
                    .build();

            return R.ok(callbackResponse);

        } catch (SsoAuthenticationException e) {
            log.error("SSO认证失败: provider={}, error={}, message={}",
                    e.getProvider(), e.getErrorCode(), e.getMessage());
            return R.fail(e.getMessage());
        }
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
     * 设置Token Cookie
     */
    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
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

    /**
     * 构建重定向URL（带Token）
     */
    private String buildRedirectUrl(String redirectUri, SsoAuthenticationResult result) {
        if (!StringUtils.hasText(redirectUri)) {
            redirectUri = "/";
        }

        // 将Token作为URL参数附加（供前端获取）
        String separator = redirectUri.contains("?") ? "&" : "?";
        return redirectUri + separator + "token=" + result.getAccessToken();
    }

    /**
     * 重定向到错误页面
     */
    private void redirectWithError(HttpServletResponse response, String redirectUri,
                                   String errorCode, String errorMessage) throws IOException {
        String targetUrl = StringUtils.hasText(redirectUri) ? redirectUri : "/";
        String separator = targetUrl.contains("?") ? "&" : "?";
        targetUrl += separator + "error=" + errorCode + "&error_description=" + errorMessage;
        response.sendRedirect(targetUrl);
    }
}
