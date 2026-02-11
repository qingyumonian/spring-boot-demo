package com.lxf.demo.modules.service.impl;

import com.lxf.demo.modules.entity.SysUser;
import com.lxf.demo.modules.service.IUserService;
import com.lxf.demo.modules.service.OidcUserSyncService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * OIDC用户同步服务实现
 * 将Keycloak用户同步到本地数据库
 */
@Slf4j
@Service
public class OidcUserSyncServiceImpl implements OidcUserSyncService {

    @Resource
    private IUserService userService;

    @Override
    public CustomUserDetails syncUser(String keycloakSub, String username, String email) {
        log.info("同步OIDC用户: username={}, sub={}", username, keycloakSub);

        // 查找本地用户
        SysUser user = userService.findByUsername(username);

        if (user == null) {
            // 创建新用户
            log.info("创建新OIDC用户: {}", username);
            user = new SysUser();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(""); // OIDC用户无本地密码
            user.setRole("USER"); // 默认角色
            user.setStatus(1); // 启用状态
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setLastLoginTime(LocalDateTime.now());
            user = userService.saveUser(user);
        } else {
            // 更新登录时间
            log.info("更新OIDC用户登录时间: {}", username);
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
