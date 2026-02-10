package com.lxf.demo.modules.auth.controller;

import com.lxf.demo.modules.auth.dto.LoginRequest;
import com.lxf.demo.modules.auth.dto.LoginResponse;
import com.lxf.demo.modules.auth.service.TokenService;
import com.lxf.demo.modules.user.service.IUserService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final String TOKEN_HEADER = "ec_access_token";

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private TokenService tokenService;

    @Resource
    private IUserService userService;

    @Value("${auth.token-timeout:3600}")
    private Long tokenTimeout;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                                HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = tokenService.saveAccessToken(userDetails);

        userService.updateLastLoginTime(userDetails.getUserId());

        Cookie cookie = new Cookie(TOKEN_HEADER, accessToken);
        cookie.setPath("/");
        cookie.setMaxAge(tokenTimeout.intValue());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(accessToken)
                .username(userDetails.getUsername())
                .role(userDetails.getRole() != null ? userDetails.getRole() : "USER")
                .expiresIn(tokenTimeout)
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request,
                                                       HttpServletResponse response) {
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            tokenService.removeAccessToken(token);
        }

        Cookie cookie = new Cookie(TOKEN_HEADER, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        SecurityContextHolder.clearContext();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Logged out successfully");
        return ResponseEntity.ok(result);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!StringUtils.hasText(token)) {
            String bearerToken = request.getHeader("Authorization");
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }
        }

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
