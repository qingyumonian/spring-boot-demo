package com.lxf.demo.config.sso.cas;

import com.lxf.demo.config.sso.SsoProperties;
import com.lxf.demo.modules.service.ITokenService;
import com.lxf.demo.security.sso.SsoUserSyncService;
import com.lxf.demo.security.sso.cas.CasAuthFailHandler;
import com.lxf.demo.security.sso.cas.CasAuthSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import javax.annotation.Resource;

/**
 * CAS安全配置
 * 使用 Spring Security 原生的 CAS 组件
 * 当sso.cas.enabled=true时自动配置CAS相关组件
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "sso.cas", name = "enabled", havingValue = "true")
public class CasSecurityConfig implements InitializingBean {

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

    @Resource
    private HttpSecurity httpSecurity;

    /**
     * CAS 服务属性配置
     * 定义本应用的 service URL（CAS 回调地址）
     */
    @Bean
    @ConditionalOnMissingBean
    public ServiceProperties serviceProperties() {
        ServiceProperties properties = new ServiceProperties();
        properties.setService(ssoProperties.getCas().getServiceUrl());
        log.info("初始化CAS ServiceProperties, service={}", properties.getService());
        return properties;
    }

    /**
     * CAS 票据验证器
     * 使用 CAS 3.0 协议验证器（兼容 CAS 2.0）
     */
    @Bean
    @ConditionalOnMissingBean
    public TicketValidator cas30ServiceTicketValidator() {
        log.info("初始化CAS票据验证器, casServerUrlPrefix={}", ssoProperties.getCas().getServerUrlPrefix());
        return new Cas30ServiceTicketValidator(ssoProperties.getCas().getServerUrlPrefix());
    }

    /**
     * CAS 认证提供者
     * 负责验证 CAS 票据并创建 Authentication 对象
     */
    @Bean
    @ConditionalOnMissingBean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();

        // 设置票据验证器
        provider.setTicketValidator(cas30ServiceTicketValidator());

        // 设置服务属性
        provider.setServiceProperties(serviceProperties());

        // 设置唯一 key
        provider.setKey("changeit");

        // 设置 AuthenticationUserDetailsService
        // 这里使用简单的实现，实际用户同步在 SuccessHandler 中处理
        provider.setAuthenticationUserDetailsService(token -> {
            String username = token.getName();
            log.info("CAS AuthenticationUserDetailsService 加载用户: {}", username);
            // 返回一个临时的 UserDetails，实际的用户同步在 SuccessHandler 中进行
            return new User(username, "", AuthorityUtils.createAuthorityList("ROLE_USER"));
        });

        log.info("初始化CAS认证提供者");
        return provider;
    }

    /**
     * CAS 认证入口点
     * 当用户未认证时，重定向到 CAS 登录页
     */
    @Bean
    @ConditionalOnMissingBean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(ssoProperties.getCas().getServerLoginUrl());
        entryPoint.setServiceProperties(serviceProperties());
        log.info("初始化CAS认证入口点, loginUrl={}", ssoProperties.getCas().getServerLoginUrl());
        return entryPoint;
    }

    /**
     * CAS 认证成功处理器
     */
    @Bean
    @ConditionalOnMissingBean
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
     * CAS 认证失败处理器
     */
//    @Bean
//    @ConditionalOnMissingBean
//    public CasAuthFailHandler casAuthFailHandler() {
//        log.info("初始化CAS认证失败处理器");
//        return new CasAuthFailHandler();
//    }

    /**
     * CAS 认证过滤器（Spring Security 原生）
     * 拦截 CAS 回调请求，验证票据
     */
    @Bean
    @ConditionalOnMissingBean(CasAuthenticationFilter.class)
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        // 设置处理的 URL 路径（默认也是 /login/cas）
        filter.setFilterProcessesUrl(ssoProperties.getCas().getLoginPath());
        // 设置成功处理器
        filter.setAuthenticationSuccessHandler(casAuthSuccessHandler());
        // 设置认证管理器
        ProviderManager providerManager = new ProviderManager(casAuthenticationProvider());
        filter.setAuthenticationManager(providerManager);
        log.info("初始化CAS认证过滤器(Spring Security原生), 处理路径: {}", ssoProperties.getCas().getLoginPath());
        return filter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        httpSecurity.addFilter(casAuthenticationFilter());
    }
}