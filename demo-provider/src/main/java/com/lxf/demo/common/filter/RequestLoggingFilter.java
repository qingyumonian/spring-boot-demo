package com.lxf.demo.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startMillis = System.currentTimeMillis();
        String method = request.getMethod();
        String path = buildRequestPath(request);
        String clientIp = resolveClientIp(request);
        LocalDateTime startTime = LocalDateTime.now();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startMillis;
            int status = response.getStatus();
            log.info("Request time={}, ip={}, method={}, path={}, status={}, costMs={}",
                startTime, clientIp, method, path, status, duration);
        }
    }

    private String buildRequestPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        return (query == null || query.isEmpty()) ? uri : uri + "?" + query;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            int commaIndex = xForwardedFor.indexOf(',');
            return commaIndex > 0 ? xForwardedFor.substring(0, commaIndex).trim() : xForwardedFor.trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }
}
