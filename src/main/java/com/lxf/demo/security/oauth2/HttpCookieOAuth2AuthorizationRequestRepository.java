package com.lxf.demo.security.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

/**
 * 基于Cookie的OAuth2授权请求存储
 * 用于无状态会话模式下的OAuth2登录
 */
@Slf4j
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private static final int COOKIE_EXPIRE_SECONDS = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        log.debug("Loading authorization request from cookie");
        return getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> {
                    try {
                        OAuth2AuthorizationRequest authRequest = deserialize(cookie, OAuth2AuthorizationRequest.class);
                        log.debug("Successfully loaded authorization request, state: {}",
                                authRequest != null ? authRequest.getState() : "null");
                        return authRequest;
                    } catch (Exception e) {
                        log.error("Failed to deserialize authorization request from cookie", e);
                        return null;
                    }
                })
                .orElseGet(() -> {
                    log.warn("No oauth2_auth_request cookie found in request");
                    return null;
                });
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            log.debug("Removing authorization request cookie");
            deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            return;
        }

        log.debug("Saving authorization request to cookie, state: {}", authorizationRequest.getState());
        String serialized = serialize(authorizationRequest);
        log.debug("Serialized authorization request length: {}", serialized.length());
        addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, serialized, COOKIE_EXPIRE_SECONDS);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        if (authorizationRequest != null) {
            log.debug("Removing authorization request cookie after successful load");
        }
        deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return authorizationRequest;
    }

    private java.util.Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    if (StringUtils.hasText(cookie.getValue())) {
                        log.debug("Found cookie '{}' with value length: {}", name, cookie.getValue().length());
                        return java.util.Optional.of(cookie);
                    }
                }
            }
        }
        log.debug("Cookie '{}' not found, available cookies: {}", name,
                cookies != null ? cookies.length : 0);
        return java.util.Optional.empty();
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // Use Set-Cookie header directly to support SameSite attribute
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(name).append("=").append(value);
        cookieBuilder.append("; Path=/");
        cookieBuilder.append("; Max-Age=").append(maxAge);
        cookieBuilder.append("; HttpOnly");
        // SameSite=Lax allows the cookie to be sent on top-level navigations (GET redirects)
        // which is what OAuth2 callback uses
        cookieBuilder.append("; SameSite=Lax");

        response.addHeader("Set-Cookie", cookieBuilder.toString());
        log.debug("Added cookie '{}' with SameSite=Lax", name);
    }

    private void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    // Use Set-Cookie header for deletion too
                    StringBuilder cookieBuilder = new StringBuilder();
                    cookieBuilder.append(name).append("=");
                    cookieBuilder.append("; Path=/");
                    cookieBuilder.append("; Max-Age=0");
                    cookieBuilder.append("; HttpOnly");
                    cookieBuilder.append("; SameSite=Lax");
                    response.addHeader("Set-Cookie", cookieBuilder.toString());
                    log.debug("Deleted cookie '{}'", name);
                }
            }
        }
    }

    private String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    private <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
