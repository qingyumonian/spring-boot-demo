package com.lxf.demo.security.sso.cas;

import com.lxf.demo.config.sso.cas.CasProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * CAS认证过滤器
 * 拦截CAS回调请求，验证票据并处理认证结果
 */
@Slf4j
public class CasAuthenticationFilter extends OncePerRequestFilter {

    private static final String TICKET_PARAM = "ticket";

    private final CasProperties casProperties;
    private final CasTicketValidator ticketValidator;
    private final CasAuthSuccessHandler successHandler;
    private final CasAuthFailHandler failHandler;

    public CasAuthenticationFilter(CasProperties casProperties,
                                   CasTicketValidator ticketValidator,
                                   CasAuthSuccessHandler successHandler,
                                   CasAuthFailHandler failHandler) {
        this.casProperties = casProperties;
        this.ticketValidator = ticketValidator;
        this.successHandler = successHandler;
        this.failHandler = failHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 只处理CAS回调路径
        if (!isCasCallbackRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ticket = request.getParameter(TICKET_PARAM);

        // 如果没有ticket参数，重定向到CAS登录页
        if (!StringUtils.hasText(ticket)) {
            log.info("无CAS票据，重定向到CAS登录页");
            redirectToCasLogin(response);
            return;
        }

        log.info("接收到CAS回调，ticket={}", ticket);

        try {
            // 验证票据
            CasUserInfo userInfo = ticketValidator.validateTicket(ticket);

            if (userInfo == null || !StringUtils.hasText(userInfo.getUsername())) {
                throw new BadCredentialsException("CAS票据验证失败");
            }

            // 认证成功
            successHandler.onAuthenticationSuccess(request, response, userInfo);

        } catch (AuthenticationException e) {
            log.error("CAS认证失败: {}", e.getMessage());
            failHandler.onAuthenticationFailure(request, response, e);
        } catch (Exception e) {
            log.error("CAS认证异常", e);
            failHandler.onAuthenticationFailure(request, response,
                    new BadCredentialsException("CAS认证异常: " + e.getMessage()));
        }
    }

    /**
     * 判断是否是CAS回调请求
     */
    private boolean isCasCallbackRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals(casProperties.getLoginPath());
    }

    /**
     * 重定向到CAS登录页
     */
    private void redirectToCasLogin(HttpServletResponse response) throws IOException {
        String casLoginUrl = casProperties.getServerLoginUrl()
                + "?service=" + encodeUrl(casProperties.getServiceUrl());
        response.sendRedirect(casLoginUrl);
    }

    /**
     * URL编码
     */
    private String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }
}
