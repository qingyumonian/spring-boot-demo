package com.lxf.demo.modules.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxf.demo.modules.auth.service.TokenProvider;
import com.lxf.demo.modules.auth.service.TokenService;
import com.lxf.demo.modules.user.entity.User;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_PREFIX = "auth:token:access:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private TokenProvider tokenProvider;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${auth.token-timeout:3600}")
    private Long tokenTimeout;

    @Override
    public String saveAccessToken(CustomUserDetails userDetails) {
        String accessToken = tokenProvider.generateToken();
        String key = TOKEN_PREFIX + accessToken;

        UserCache userCache = new UserCache();
        userCache.setId(userDetails.getUserId());
        userCache.setUsername(userDetails.getUsername());
        userCache.setRole(userDetails.getRole());
        userCache.setStatus(userDetails.getUser().getStatus());

        redisTemplate.opsForValue().set(key, userCache, tokenTimeout, TimeUnit.SECONDS);
        return accessToken;
    }

    @Override
    public CustomUserDetails getUserByToken(String token) {
        String key = TOKEN_PREFIX + token;
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }

        UserCache userCache;
        if (value instanceof LinkedHashMap) {
            userCache = objectMapper.convertValue(value, UserCache.class);
        } else if (value instanceof UserCache) {
            userCache = (UserCache) value;
        } else {
            return null;
        }

        User user = new User();
        user.setId(userCache.getId());
        user.setUsername(userCache.getUsername());
        user.setRole(userCache.getRole());
        user.setStatus(userCache.getStatus());

        return new CustomUserDetails(user);
    }

    @Override
    public void removeAccessToken(String token) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }

    @Override
    public boolean validateToken(String token) {
        String key = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public static class UserCache {
        private Long id;
        private String username;
        private String role;
        private Integer status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }
}
