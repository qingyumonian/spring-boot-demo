package com.lxf.demo.security.sso.cas;

import com.lxf.demo.config.sso.SsoProperties;
import com.lxf.demo.config.sso.cas.CasProperties;
import com.lxf.demo.security.sso.AbstractSsoProvider;
import com.lxf.demo.security.sso.SsoAuthenticationException;
import com.lxf.demo.security.sso.SsoAuthenticationResult;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * CAS SSO提供商实现
 * 支持CAS 2.0/3.0协议
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sso.cas", name = "enabled", havingValue = "true")
public class CasSsoProvider extends AbstractSsoProvider {

    private static final String PROVIDER_NAME = "cas";
    private static final String CALLBACK_PATH = "/api/auth/sso/cas/callback";

    @Resource
    private SsoProperties ssoProperties;

    private CasProperties casProperties;
    private Cas30ServiceTicketValidator ticketValidator;

    @PostConstruct
    public void init() {
        this.casProperties = ssoProperties.getCas();
        this.ticketValidator = new Cas30ServiceTicketValidator(casProperties.getServerUrlPrefix());
        log.info("CAS SSO Provider 初始化完成: serverUrl={}", casProperties.getServerUrlPrefix());
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isEnabled() {
        return casProperties.isEnabled();
    }

    @Override
    public String getLoginUrl(String redirectUri, String state) {
        String serviceUrl = buildServiceUrl(redirectUri, state);
        String loginUrl = casProperties.getServerLoginUrl() + "?service=" + urlEncode(serviceUrl);
        log.debug("生成CAS登录URL: {}", loginUrl);
        return loginUrl;
    }

    @Override
    public SsoAuthenticationResult handleCallback(HttpServletRequest request) throws SsoAuthenticationException {
        String ticket = request.getParameter("ticket");
        if (!StringUtils.hasText(ticket)) {
            throw new SsoAuthenticationException(PROVIDER_NAME, "MISSING_TICKET", "缺少CAS ticket参数");
        }

        String redirectUri = request.getParameter("redirectUri");
        String state = request.getParameter("state");
        String serviceUrl = buildServiceUrl(redirectUri, state);

        try {
            log.info("验证CAS ticket: {}", ticket);
            Assertion assertion = ticketValidator.validate(ticket, serviceUrl);

            String username = assertion.getPrincipal().getName();
            Map<String, Object> attributes = assertion.getPrincipal().getAttributes();

            // 从CAS属性中获取邮箱（可能为null）
            String email = getAttributeValue(attributes, "email", "mail");

            log.info("CAS认证成功: username={}", username);
            return createAuthResult(username, username, email);

        } catch (TicketValidationException e) {
            log.error("CAS ticket验证失败: {}", e.getMessage());
            throw new SsoAuthenticationException(PROVIDER_NAME, "TICKET_VALIDATION_FAILED",
                    "CAS ticket验证失败: " + e.getMessage(), e);
        }
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
     */
    private String buildServiceUrl(String redirectUri, String state) {
        StringBuilder serviceUrl = new StringBuilder();
        serviceUrl.append(casProperties.getClientHostUrl());
        serviceUrl.append(CALLBACK_PATH);

        boolean hasParam = false;
        if (StringUtils.hasText(redirectUri)) {
            serviceUrl.append("?redirectUri=").append(urlEncode(redirectUri));
            hasParam = true;
        }
        if (StringUtils.hasText(state)) {
            serviceUrl.append(hasParam ? "&" : "?");
            serviceUrl.append("state=").append(urlEncode(state));
        }

        return serviceUrl.toString();
    }

    /**
     * 从属性Map中获取值（尝试多个可能的属性名）
     */
    private String getAttributeValue(Map<String, Object> attributes, String... keys) {
        if (attributes == null) {
            return null;
        }
        for (String key : keys) {
            Object value = attributes.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
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
