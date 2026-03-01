package com.lxf.demo.config.sso.oidc;

import lombok.Data;

/**
 * Keycloak OIDC配置属性
 *
 * 注意：OAuth2 客户端配置现在由 Spring Security 标准配置管理：
 * spring.security.oauth2.client.registration.keycloak.*
 * spring.security.oauth2.client.provider.keycloak.*
 *
 * 本类只保留额外的配置项（enabled, defaultRole, logoutUri）
 */
@Data
public class KeycloakProperties {

    /**
     * 是否启用Keycloak OIDC认证
     */
    private boolean enabled = false;

    /**
     * 新用户默认角色
     */
    private String defaultRole = "USER";

    /**
     * 登出端点URI
     * 例如: http://localhost:8080/realms/selfTest/protocol/openid-connect/logout
     */
    private String logoutUri;
}
