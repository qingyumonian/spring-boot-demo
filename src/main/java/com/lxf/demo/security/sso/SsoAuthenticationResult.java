package com.lxf.demo.security.sso;

import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.Builder;
import lombok.Data;

/**
 * SSO认证结果
 * 封装SSO认证成功后的用户信息和令牌
 */
@Data
@Builder
public class SsoAuthenticationResult {

    /**
     * SSO提供商名称
     */
    private String provider;

    /**
     * 用户详情
     */
    private CustomUserDetails userDetails;

    /**
     * 本地访问令牌
     */
    private String accessToken;

    /**
     * 令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 外部系统用户ID
     */
    private String externalUserId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;
}
