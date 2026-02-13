package com.lxf.demo.config.sso.cas;

import com.lxf.demo.config.sso.SsoProperties;
import com.lxf.demo.modules.service.ITokenService;
import com.lxf.demo.security.sso.SsoUserSyncService;
import com.lxf.demo.security.sso.cas.CasAuthFailHandler;
import com.lxf.demo.security.sso.cas.CasAuthSuccessHandler;
import com.lxf.demo.security.sso.cas.CasAuthenticationFilter;
import com.lxf.demo.security.sso.cas.CasTicketValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * CAS安全配置
 * 当sso.cas.enabled=true时自动配置CAS相关组件
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "sso.cas", name = "enabled", havingValue = "true")
public class CasSecurityConfig {

    @Resource
    private SsoProperties ssoProperties;

    @Resource
    private SsoUserSyncService ssoUserSyncService;

    @Resource
    private ITokenService tokenService;

    @Value("${auth.token-timeout:3600}")
    private Long tokenTimeout;

    @Value("${auth.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * CAS票据验证器
     */
    @Bean
    public CasTicketValidator casTicketValidator() {
        log.info("初始化CAS票据验证器");
        return new CasTicketValidator(ssoProperties.getCas());
    }

    /**
     * CAS认证成功处理器
     */
    @Bean
    public CasAuthSuccessHandler casAuthSuccessHandler() {
        log.info("初始化CAS认证成功处理器");
        return new CasAuthSuccessHandler(
                ssoProperties.getCas(),
                ssoUserSyncService,
                tokenService,
                tokenTimeout,
                frontendUrl
        );
    }

    /**
     * CAS认证失败处理器
     */
    @Bean
    public CasAuthFailHandler casAuthFailHandler() {
        log.info("初始化CAS认证失败处理器");
        return new CasAuthFailHandler();
    }

    /**
     * CAS认证过滤器
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() {
        log.info("初始化CAS认证过滤器, 回调路径: {}", ssoProperties.getCas().getLoginPath());
        return new CasAuthenticationFilter(
                ssoProperties.getCas(),
                casTicketValidator(),
                casAuthSuccessHandler(),
                casAuthFailHandler()
        );
    }
}
