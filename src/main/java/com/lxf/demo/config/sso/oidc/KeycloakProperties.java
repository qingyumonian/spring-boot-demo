package com.lxf.demo.config.sso.oidc;

import lombok.Data;

/**
 * Keycloak OIDC配置属性
 */
@Data
public class KeycloakProperties {

    /**
     * 是否启用Keycloak OIDC认证
     */
    private boolean enabled = false;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * Keycloak realm名称
     */
    private String realm;

    /**
     * Keycloak服务器地址
     * 例如: http://localhost:8080
     */
    private String baseUrl;

    /**
     * 重定向URI
     * 例如: http://localhost:8888/api/auth/keycloak
     */
    private String redirectUri;

    /**
     * 新用户默认角色
     */
    private String defaultRole = "USER";

    /**
     * 获取授权端点URI
     */
    public String getAuthorizationUri() {
        return baseUrl + "/realms/" + realm + "/protocol/openid-connect/auth";
    }

    /**
     * 获取令牌端点URI
     */
    public String getTokenUri() {
        return baseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    /**
     * 获取用户信息端点URI
     */
    public String getUserInfoUri() {
        return baseUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
    }

    /**
     * 获取JWK Set URI
     */
    public String getJwkSetUri() {
        return baseUrl + "/realms/" + realm + "/protocol/openid-connect/certs";
    }

    /**
     * 获取登出端点URI
     */
    public String getLogoutEndpoint() {
        return baseUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
    }
}
