package com.lxf.demo.modules.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SSO回调响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoCallbackResponse {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户名
     */
    private String username;

    /**
     * 令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * SSO提供商名称
     */
    private String provider;
}
