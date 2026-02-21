package com.lxf.demo.security.sso.oidc;

import com.lxf.demo.config.sso.SsoProperties;
import com.lxf.demo.config.sso.oidc.KeycloakProperties;
import com.lxf.demo.security.sso.AbstractSsoProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Keycloak OIDC SSO提供商实现
 *
 * 登录流程由 Spring Security OAuth2 Filter 链处理：
 * 1. OAuth2AuthorizationRequestRedirectFilter 处理 /oauth2/authorization/keycloak
 *    - 创建 OAuth2AuthorizationRequest 并保存到 Cookie
 *    - 重定向到 Keycloak 授权页
 * 2. OAuth2LoginAuthenticationFilter 处理回调 /login/oauth2/code/keycloak
 *    - 从 Cookie 加载保存的 OAuth2AuthorizationRequest
 *    - 使用 code 换取 token
 * 3. OidcAuthSuccessHandler 处理认证成功
 *    - 同步用户到本地数据库
 *    - 生成本地 Token
 *    - 重定向到前端
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sso.keycloak", name = "enabled", havingValue = "true")
public class KeycloakSsoProvider extends AbstractSsoProvider {

    private static final String PROVIDER_NAME = "keycloak";
    private static final String REGISTRATION_ID = "keycloak";

    @Resource
    private SsoProperties ssoProperties;

    @Resource
    private ClientRegistrationRepository clientRegistrationRepository;

    private KeycloakProperties keycloakProperties;
    private ClientRegistration clientRegistration;

    @PostConstruct
    public void init() {
        this.keycloakProperties = ssoProperties.getKeycloak();
        this.clientRegistration = clientRegistrationRepository.findByRegistrationId(REGISTRATION_ID);

        if (clientRegistration != null) {
            log.info("Keycloak SSO Provider 初始化完成: clientId={}, authUri={}",
                    clientRegistration.getClientId(),
                    clientRegistration.getProviderDetails().getAuthorizationUri());
        } else {
            log.warn("未找到 Keycloak ClientRegistration，请检查 spring.security.oauth2.client 配置");
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isEnabled() {
        return keycloakProperties.isEnabled();
    }

    /**
     * 生成 Keycloak 登录 URL
     *
     * 返回 Spring Security OAuth2 的标准授权端点 URL：/oauth2/authorization/keycloak
     * 这样 Spring Security 会自动处理整个 OAuth2 流程
     */
    @Override
    public String getLoginUrl(String redirectUri, String state) {
        // 返回 Spring Security OAuth2 的标准授权端点
        String loginUrl = "/oauth2/authorization/" + REGISTRATION_ID;
        log.info("生成Keycloak登录URL: {} (使用Spring Security OAuth2标准流程)", loginUrl);
        return loginUrl;
    }

    @Override
    public String getLogoutUrl(String postLogoutRedirectUri) {
        String logoutUri = keycloakProperties.getLogoutUri();
        if (!StringUtils.hasText(logoutUri)) {
            log.warn("未配置 sso.keycloak.logout-uri");
            return "/";
        }

        StringBuilder logoutUrl = new StringBuilder(logoutUri);
        logoutUrl.append("?client_id=").append(urlEncode(clientRegistration.getClientId()));

        if (StringUtils.hasText(postLogoutRedirectUri)) {
            logoutUrl.append("&post_logout_redirect_uri=").append(urlEncode(postLogoutRedirectUri));
        }

        return logoutUrl.toString();
    }

    @Override
    protected String getDefaultRole() {
        return keycloakProperties.getDefaultRole();
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
