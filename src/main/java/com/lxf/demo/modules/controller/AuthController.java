package com.lxf.demo.modules.controller;

import com.lxf.demo.common.result.R;
import com.lxf.demo.modules.service.IRoleService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证控制器
 * 实际认证由Spring Security处理
 * 登录端点: POST /api/auth/form
 * 登出端点: POST /api/auth/logout
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String TOKEN_HEADER = "am_access_token";

    @Resource
    private IRoleService.TokenService tokenService;

    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // 从请求中获取token
        String token = getTokenFromRequest(request);

        // 从Redis中移除token
        if (StringUtils.hasText(token)) {
            tokenService.removeAccessToken(token);
        }

        // 清除浏览器中的token cookie
        Cookie cookie = new Cookie(TOKEN_HEADER, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return R.ok();
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从header中获取
        String token = request.getHeader(TOKEN_HEADER);

        // 2. 从Authorization header中获取
        if (!StringUtils.hasText(token)) {
            String bearerToken = request.getHeader("Authorization");
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }
        }

        // 3. 从cookie中获取
        if (!StringUtils.hasText(token)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (TOKEN_HEADER.equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        return token;
    }
}