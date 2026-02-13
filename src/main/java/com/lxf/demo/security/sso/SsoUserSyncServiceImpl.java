package com.lxf.demo.security.sso;

import com.lxf.demo.modules.entity.SysUser;
import com.lxf.demo.modules.service.IUserService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 统一SSO用户同步服务实现
 * 支持多种SSO提供商（CAS/OIDC/...）用户同步到本地数据库
 */
@Slf4j
@Service
public class SsoUserSyncServiceImpl implements SsoUserSyncService {

    private static final String DEFAULT_ROLE = "USER";

    @Resource
    private IUserService userService;

    @Override
    public CustomUserDetails syncUser(String provider, String externalId, String username, String email) {
        return syncUser(provider, externalId, username, email, DEFAULT_ROLE);
    }

    @Override
    public CustomUserDetails syncUser(String provider, String externalId, String username, String email, String defaultRole) {
        log.info("同步SSO用户: provider={}, username={}, externalId={}", provider, username, externalId);

        // 查找本地用户
        SysUser user = userService.findByUsername(username);

        if (user == null) {
            // 创建新用户
            log.info("创建新SSO用户: provider={}, username={}", provider, username);
            user = new SysUser();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(""); // SSO用户无本地密码
            user.setRole(defaultRole != null ? defaultRole : DEFAULT_ROLE);
            user.setStatus(1); // 启用状态
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setLastLoginTime(LocalDateTime.now());
            user = userService.saveUser(user);
        } else {
            // 更新登录时间
            log.info("更新SSO用户登录时间: provider={}, username={}", provider, username);
            // 可选：更新邮箱（如果SSO提供的邮箱有变化）
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
                user.setUpdatedAt(LocalDateTime.now());
            }
            userService.updateLastLoginTime(user.getId());
        }

        // 获取用户权限
        Set<String> permissions = userService.getPermissionsByUserId(user.getId());

        // 构建CustomUserDetails
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword() != null ? user.getPassword() : "",
                permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}
