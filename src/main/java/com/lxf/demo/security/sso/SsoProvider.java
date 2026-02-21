package com.lxf.demo.security.sso;

import javax.servlet.http.HttpServletRequest;

/**
 * SSO提供商策略接口
 * 统一抽象CAS和OIDC等不同SSO协议的登录流程
 */
public interface SsoProvider {

    /**
     * 获取提供商名称
     * @return 提供商标识符（如: "cas", "keycloak"）
     */
    String getProviderName();

    /**
     * 检查该提供商是否启用
     * @return true表示已启用
     */
    boolean isEnabled();

    /**
     * 获取SSO登录URL
     * @param redirectUri 登录成功后的重定向URI
     * @param state 状态参数（用于防止CSRF攻击）
     * @return 完整的SSO登录URL
     */
    String getLoginUrl(String redirectUri, String state);


    /**
     * 获取SSO登出URL
     * @param postLogoutRedirectUri 登出后的重定向URI
     * @return 完整的SSO登出URL
     */
    String getLogoutUrl(String postLogoutRedirectUri);
}
