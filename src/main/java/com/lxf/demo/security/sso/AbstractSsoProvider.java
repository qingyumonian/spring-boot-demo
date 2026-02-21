package com.lxf.demo.security.sso;

import com.lxf.demo.modules.service.ITokenService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * SSO提供商抽象基类
 * 提供通用逻辑，如用户同步和Token生成
 */
@Slf4j
public abstract class AbstractSsoProvider implements SsoProvider {

    /**
     * 默认Token过期时间（7200秒 = 2小时）
     */
    protected static final Long DEFAULT_TOKEN_EXPIRES_IN = 7200L;

    @Resource
    protected SsoUserSyncService ssoUserSyncService;

    @Resource
    protected ITokenService tokenService;

    /**
     * 创建认证结果
     * @param externalUserId 外部用户ID
     * @param username 用户名
     * @param email 邮箱
     * @param defaultRole 默认角色
     * @return 认证结果
     */
    protected SsoAuthenticationResult createAuthResult(String externalUserId, String username, String email, String defaultRole) {
        log.info("创建SSO认证结果: provider={}, username={}, externalUserId={}",
                getProviderName(), username, externalUserId);

        // 同步用户到本地数据库
        CustomUserDetails userDetails = ssoUserSyncService.syncUser(
                getProviderName(),
                externalUserId,
                username,
                email,
                defaultRole
        );

        // 生成本地访问令牌
        String accessToken = tokenService.saveAccessToken(userDetails);

        return SsoAuthenticationResult.builder()
                .provider(getProviderName())
                .userDetails(userDetails)
                .accessToken(accessToken)
                .expiresIn(DEFAULT_TOKEN_EXPIRES_IN)
                .externalUserId(externalUserId)
                .username(username)
                .email(email)
                .build();
    }

    /**
     * 创建认证结果（使用默认角色）
     * @param externalUserId 外部用户ID
     * @param username 用户名
     * @param email 邮箱
     * @return 认证结果
     */
    protected SsoAuthenticationResult createAuthResult(String externalUserId, String username, String email) {
        return createAuthResult(externalUserId, username, email, getDefaultRole());
    }

    /**
     * 获取默认角色
     * 子类可重写此方法返回特定的默认角色
     * @return 默认角色
     */
    protected abstract String getDefaultRole();
}
