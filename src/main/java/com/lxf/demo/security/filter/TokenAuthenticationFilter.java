package com.lxf.demo.security.filter;

import com.lxf.demo.modules.service.IRoleService;
import com.lxf.demo.security.AuthenticationToken;
import com.lxf.demo.security.handler.FormAuthFailHandler;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "am_access_token";

    @Resource
    private IRoleService.TokenService tokenService;
    @Resource
    private FormAuthFailHandler authenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token) && tokenService.validateToken(token)) {
                CustomUserDetails userDetails = tokenService.getUserByToken(token);
                AuthenticationToken authentication = null;
                if (userDetails != null) {
                     authentication = new AuthenticationToken(userDetails, token);
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                System.out.println("Is user authenticated? " + authentication.isAuthenticated());
            }
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            this.authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
        } catch (Exception e) {
            log.error("Cannot set user authentication", e);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!StringUtils.hasText(token)) {
            String bearerToken = request.getHeader("Authorization");
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }
        }

        if (!StringUtils.hasText(token)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (TOKEN_HEADER.equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        return token;
    }
}
