package com.lxf.demo.config.sso.oidc;

import com.lxf.demo.config.sso.SsoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import javax.annotation.Resource;

/**
 * Keycloak OIDC安全配置
 * 当sso.keycloak.enabled=true时动态创建ClientRegistrationRepository
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "sso.keycloak", name = "enabled", havingValue = "true")
public class KeycloakSecurityConfig {

    @Resource
    private SsoProperties ssoProperties;

    /**
     * 动态创建ClientRegistrationRepository
     * 替代application.yml中的spring.security.oauth2.client配置
     */
    @Bean
    @Primary
    public ClientRegistrationRepository clientRegistrationRepository() {
        KeycloakProperties keycloak = ssoProperties.getKeycloak();
        log.info("动态创建Keycloak ClientRegistration: clientId={}, realm={}",
                keycloak.getClientId(), keycloak.getRealm());

        ClientRegistration registration = ClientRegistration.withRegistrationId("keycloak")
                .clientId(keycloak.getClientId())
                .clientSecret(keycloak.getClientSecret())
                .scope("openid")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate(keycloak.getRedirectUri())
                .authorizationUri(keycloak.getAuthorizationUri())
                .tokenUri(keycloak.getTokenUri())
                .userInfoUri(keycloak.getUserInfoUri())
                .jwkSetUri(keycloak.getJwkSetUri())
                .userNameAttributeName("preferred_username")
                .clientName("Keycloak")
                .build();

        return new InMemoryClientRegistrationRepository(registration);
    }
}
