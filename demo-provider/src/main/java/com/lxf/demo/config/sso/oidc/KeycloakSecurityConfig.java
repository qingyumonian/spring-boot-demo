package com.lxf.demo.config.sso.oidc;

import com.lxf.demo.config.sso.SsoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Keycloak OIDC安全配置
 *
 * 当 sso.keycloak.enabled=true 时启用
 *
 * 注意：ClientRegistrationRepository 由 Spring Boot 根据以下配置自动创建：
 * spring.security.oauth2.client.registration.keycloak.*
 * spring.security.oauth2.client.provider.keycloak.*
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "sso.keycloak", name = "enabled", havingValue = "true")
public class KeycloakSecurityConfig {

    @Resource
    private SsoProperties ssoProperties;

    @PostConstruct
    public void init() {
        KeycloakProperties keycloak = ssoProperties.getKeycloak();
        log.info("Keycloak OIDC 已启用: defaultRole={}", keycloak.getDefaultRole());
    }
}
