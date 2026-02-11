package com.lxf.demo.modules.service;

import com.lxf.demo.security.userdetails.CustomUserDetails;

/**
 * OIDC用户同步服务接口
 * 用于同步Keycloak用户到本地数据库
 */
public interface OidcUserSyncService {

    /**
     * 同步OIDC用户到本地数据库
     *
     * @param keycloakSub Keycloak用户唯一标识 (sub claim)
     * @param username 用户名 (preferred_username claim)
     * @param email 邮箱
     * @return CustomUserDetails 用户详情对象
     */
    CustomUserDetails syncUser(String keycloakSub, String username, String email);
}
