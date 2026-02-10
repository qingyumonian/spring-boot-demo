package com.lxf.demo.modules.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 实际认证由Spring Security处理
 * 登录端点: POST /api/auth/form
 * 登出端点: POST /api/logout
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    // 认证逻辑由Spring Security框架自动处理
}