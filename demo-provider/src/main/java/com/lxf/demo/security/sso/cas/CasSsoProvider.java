package com.lxf.demo.security.sso.cas;

import com.lxf.demo.config.sso.SsoProperties;
import com.lxf.demo.config.sso.cas.CasProperties;
import com.lxf.demo.security.sso.AbstractSsoProvider;
import com.lxf.demo.security.sso.SsoAuthenticationException;
import com.lxf.demo.security.sso.SsoAuthenticationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * CAS SSO提供商实现
 * 支持CAS 2.0/3.0协议
 *
 * 注意：CAS 回调由 Spring Security 原生的 CasAuthenticationFilter 处理（/login/cas），
 * 本类只负责生成登录/登出 URL，不处理回调
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sso.cas", name = "enabled", havingValue = "true")
public class CasSsoProvider extends AbstractSsoProvider {

    private static final String PROVIDER_NAME = "cas";

    @Resource
    private SsoProperties ssoProperties;

    private CasProperties casProperties;

    @PostConstruct
    public void init() {
        this.casProperties = ssoProperties.getCas();
        log.info("CAS SSO Provider 初始化完成: serverUrl={}, filterPath={}",
                casProperties.getServerUrlPrefix(), casProperties.getLoginPath());
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isEnabled() {
        return casProperties.isEnabled();
    }

    /**
     * 生成 CAS 登录 URL
     * service 参数指向 CasAuthenticationFilter 处理的路径（/login/cas）
     */
    @Override
    public String getLoginUrl(String redirectUri, String state) {
        String serviceUrl = buildServiceUrl();
        String loginUrl = casProperties.getServerLoginUrl() + "?service=" + urlEncode(serviceUrl);
        log.info("生成CAS登录URL: {}", loginUrl);
        return loginUrl;
    }

    @Override
    public String getLogoutUrl(String postLogoutRedirectUri) {
        String logoutUrl = casProperties.getServerUrlPrefix() + "/logout";
        if (StringUtils.hasText(postLogoutRedirectUri)) {
            logoutUrl += "?service=" + urlEncode(postLogoutRedirectUri);
        }
        return logoutUrl;
    }

    @Override
    protected String getDefaultRole() {
        return casProperties.getDefaultRole();
    }

    /**
     * 构建服务URL（CAS回调URL）
     * 指向 CasAuthenticationFilter 处理的路径
     */
    private String buildServiceUrl() {
        return casProperties.getClientHostUrl() + casProperties.getLoginPath();
    }

    /**
     * URL编码
     */
    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
