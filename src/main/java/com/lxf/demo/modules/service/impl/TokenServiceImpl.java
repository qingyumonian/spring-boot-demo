package com.lxf.demo.modules.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxf.demo.modules.service.IUserService;
import com.lxf.demo.modules.entity.SysUser;
import com.lxf.demo.modules.service.ITokenService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class TokenServiceImpl implements ITokenService {

    private static final String TOKEN_PREFIX = "auth:token:access:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MenuServiceImpl.TokenProvider tokenProvider;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private IUserService userService;

    @Value("${auth.token-timeout:3600}")
    private Long tokenTimeout;

    @Override
    public String saveAccessToken(CustomUserDetails userDetails) {
        String accessToken = tokenProvider.generateToken();
        String key = TOKEN_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, userDetails, tokenTimeout, TimeUnit.SECONDS);
        return accessToken;
    }

    @Override
    public CustomUserDetails getUserByToken(String token) {
        String key = TOKEN_PREFIX + token;
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }

        CustomUserDetails userCache;
        Long userId = 0L;
        if (value instanceof LinkedHashMap) {
            userId = Long.valueOf(((Integer) ((LinkedHashMap<?, ?>) value).get("id")).toString());
        } else if (value instanceof CustomUserDetails) {
            userCache = (CustomUserDetails) value;
            userId = userCache.getId();
        } else {
            return null;
        }
        // 实时查询用户权限
        SysUser user = userService.getUserById(userId);
        Set<String> permissions = userService.getPermissionsByUserId(user.getId());
        String[] permArray = permissions.stream().filter(StringUtils::isNotBlank).toArray(String[]::new);
        return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(permArray));
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


}
