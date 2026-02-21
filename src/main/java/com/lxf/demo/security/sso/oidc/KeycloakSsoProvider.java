package com.lxf.demo.security.sso.oidc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxf.demo.config.sso.SsoProperties;
import com.lxf.demo.config.sso.oidc.KeycloakProperties;
import com.lxf.demo.security.sso.AbstractSsoProvider;
import com.lxf.demo.security.sso.SsoAuthenticationException;
import com.lxf.demo.security.sso.SsoAuthenticationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Keycloak OIDC SSO提供商实现
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sso.keycloak", name = "enabled", havingValue = "true")
public class KeycloakSsoProvider extends AbstractSsoProvider {

    private static final String PROVIDER_NAME = "keycloak";
    private static final String CALLBACK_PATH = "/api/auth/sso/keycloak/callback";

    @Resource
    private SsoProperties ssoProperties;

    private KeycloakProperties keycloakProperties;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        this.keycloakProperties = ssoProperties.getKeycloak();
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        log.info("Keycloak SSO Provider 初始化完成: baseUrl={}, realm={}",
                keycloakProperties.getBaseUrl(), keycloakProperties.getRealm());
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isEnabled() {
        return keycloakProperties.isEnabled();
    }

    @Override
    public String getLoginUrl(String redirectUri, String state) {
        String callbackUrl = buildCallbackUrl(redirectUri);

        StringBuilder authUrl = new StringBuilder();
        authUrl.append(keycloakProperties.getAuthorizationUri());
        authUrl.append("?client_id=").append(urlEncode(keycloakProperties.getClientId()));
        authUrl.append("&response_type=code");
        authUrl.append("&scope=").append(urlEncode("openid profile email"));
        authUrl.append("&redirect_uri=").append(urlEncode(callbackUrl));

        if (StringUtils.hasText(state)) {
            authUrl.append("&state=").append(urlEncode(state));
        } else {
            authUrl.append("&state=").append(UUID.randomUUID().toString());
        }

        String loginUrl = authUrl.toString();
        log.debug("生成Keycloak登录URL: {}", loginUrl);
        return loginUrl;
    }

    @Override
    public SsoAuthenticationResult handleCallback(HttpServletRequest request) throws SsoAuthenticationException {
        String code = request.getParameter("code");
        String error = request.getParameter("error");
        String errorDescription = request.getParameter("error_description");

        if (StringUtils.hasText(error)) {
            log.error("Keycloak返回错误: error={}, description={}", error, errorDescription);
            throw new SsoAuthenticationException(PROVIDER_NAME, error,
                    StringUtils.hasText(errorDescription) ? errorDescription : error);
        }

        if (!StringUtils.hasText(code)) {
            throw new SsoAuthenticationException(PROVIDER_NAME, "MISSING_CODE", "缺少授权码参数");
        }

        String redirectUri = request.getParameter("redirectUri");
        String callbackUrl = buildCallbackUrl(redirectUri);

        try {
            // 1. 使用授权码交换Token
            JsonNode tokenResponse = exchangeCodeForToken(code, callbackUrl);
            String accessToken = tokenResponse.get("access_token").asText();

            // 2. 使用Token获取用户信息
            JsonNode userInfo = fetchUserInfo(accessToken);

            String sub = userInfo.get("sub").asText();
            String username = userInfo.has("preferred_username")
                    ? userInfo.get("preferred_username").asText()
                    : sub;
            String email = userInfo.has("email")
                    ? userInfo.get("email").asText()
                    : null;

            log.info("Keycloak认证成功: username={}, sub={}", username, sub);
            return createAuthResult(sub, username, email);

        } catch (SsoAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Keycloak认证失败: {}", e.getMessage(), e);
            throw new SsoAuthenticationException(PROVIDER_NAME, "AUTH_FAILED",
                    "Keycloak认证失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getLogoutUrl(String postLogoutRedirectUri) {
        StringBuilder logoutUrl = new StringBuilder();
        logoutUrl.append(keycloakProperties.getLogoutEndpoint());
        logoutUrl.append("?client_id=").append(urlEncode(keycloakProperties.getClientId()));

        if (StringUtils.hasText(postLogoutRedirectUri)) {
            logoutUrl.append("&post_logout_redirect_uri=").append(urlEncode(postLogoutRedirectUri));
        }

        return logoutUrl.toString();
    }

    @Override
    protected String getDefaultRole() {
        return keycloakProperties.getDefaultRole();
    }

    /**
     * 构建回调URL
     */
    private String buildCallbackUrl(String redirectUri) {
        StringBuilder callbackUrl = new StringBuilder();
        // 使用配置的redirectUri或构建默认的
        if (StringUtils.hasText(keycloakProperties.getRedirectUri())) {
            // 使用配置的基础部分
            String baseRedirectUri = keycloakProperties.getRedirectUri();
            // 替换路径为新的SSO回调路径
            int pathIndex = baseRedirectUri.indexOf("/api/");
            if (pathIndex > 0) {
                callbackUrl.append(baseRedirectUri.substring(0, pathIndex));
            } else {
                callbackUrl.append(baseRedirectUri);
            }
        } else {
            callbackUrl.append("http://localhost:8888");
        }
        callbackUrl.append(CALLBACK_PATH);

        if (StringUtils.hasText(redirectUri)) {
            callbackUrl.append("?redirectUri=").append(urlEncode(redirectUri));
        }

        return callbackUrl.toString();
    }

    /**
     * 使用授权码交换Token
     */
    private JsonNode exchangeCodeForToken(String code, String redirectUri) throws SsoAuthenticationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", keycloakProperties.getClientId());
        params.add("client_secret", keycloakProperties.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    keycloakProperties.getTokenUri(),
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new SsoAuthenticationException(PROVIDER_NAME, "TOKEN_EXCHANGE_FAILED",
                        "Token交换失败: HTTP " + response.getStatusCode());
            }

            return objectMapper.readTree(response.getBody());

        } catch (RestClientException e) {
            throw new SsoAuthenticationException(PROVIDER_NAME, "TOKEN_EXCHANGE_FAILED",
                    "Token交换失败: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SsoAuthenticationException(PROVIDER_NAME, "TOKEN_PARSE_FAILED",
                    "Token解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户信息
     */
    private JsonNode fetchUserInfo(String accessToken) throws SsoAuthenticationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    keycloakProperties.getUserInfoUri(),
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new SsoAuthenticationException(PROVIDER_NAME, "USERINFO_FAILED",
                        "获取用户信息失败: HTTP " + response.getStatusCode());
            }

            return objectMapper.readTree(response.getBody());

        } catch (RestClientException e) {
            throw new SsoAuthenticationException(PROVIDER_NAME, "USERINFO_FAILED",
                    "获取用户信息失败: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SsoAuthenticationException(PROVIDER_NAME, "USERINFO_PARSE_FAILED",
                    "用户信息解析失败: " + e.getMessage(), e);
        }
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
