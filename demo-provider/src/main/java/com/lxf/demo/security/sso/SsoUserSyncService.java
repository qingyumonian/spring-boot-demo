package com.lxf.demo.security.sso;

import com.lxf.demo.security.userdetails.CustomUserDetails;

/**
 * 统一SSO用户同步服务接口
 * 用于将外部认证提供商（CAS/OIDC等）的用户同步到本地数据库
 */
public interface SsoUserSyncService {

    /**
     * 同步外部认证用户到本地数据库
     *
     * @param provider   认证提供商标识（如: "cas", "keycloak"）
     * @param externalId 外部系统用户唯一标识
     * @param username   用户名
     * @param email      邮箱（可为null）
     * @return CustomUserDetails 用户详情对象
     */
    CustomUserDetails syncUser(String provider, String externalId, String username, String email);

    /**
     * 同步外部认证用户到本地数据库（使用默认角色）
     *
     * @param provider    认证提供商标识
     * @param externalId  外部系统用户唯一标识
     * @param username    用户名
     * @param email       邮箱（可为null）
     * @param defaultRole 默认角色
     * @return CustomUserDetails 用户详情对象
     */
    CustomUserDetails syncUser(String provider, String externalId, String username, String email, String defaultRole);
}
