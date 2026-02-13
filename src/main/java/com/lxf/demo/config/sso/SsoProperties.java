package com.lxf.demo.config.sso;

import com.lxf.demo.config.sso.cas.CasProperties;
import com.lxf.demo.config.sso.oidc.KeycloakProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

/**
 * SSO总配置
 * 聚合所有SSO提供商的配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sso")
public class SsoProperties {

    /**
     * CAS配置
     */
    @NestedConfigurationProperty
    private CasProperties cas = new CasProperties();

    /**
     * Keycloak OIDC配置
     */
    @NestedConfigurationProperty
    private KeycloakProperties keycloak = new KeycloakProperties();
}
