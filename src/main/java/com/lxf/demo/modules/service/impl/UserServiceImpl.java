package com.lxf.demo.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxf.demo.modules.entity.SysMenu;
import com.lxf.demo.modules.entity.SysUserRole;
import com.lxf.demo.modules.mapper.MenuMapper;
import com.lxf.demo.modules.mapper.RoleMenuMapper;
import com.lxf.demo.modules.mapper.UserRoleMapper;
import com.lxf.demo.modules.entity.SysUser;
import com.lxf.demo.modules.mapper.UserMapper;
import com.lxf.demo.modules.service.IUserService;
import com.lxf.demo.security.encoder.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private Md5PasswordEncoder md5PasswordEncoder;

    @Override
    public SysUser saveUser(SysUser user) {
        if (user.getPassword() != null) {
            user.setPassword(md5PasswordEncoder.encode(user.getPassword()));
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        userMapper.insert(user);
        return user;
    }

    @Override
    public SysUser getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public SysUser updateUser(SysUser user) {
        userMapper.updateById(user);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public List<SysUser> getAllUsers() {
        return userMapper.selectList(null);
    }

    @Override
    public IPage<SysUser> getUserPage(int pageNum, int pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        return userMapper.selectPage(page, null);
    }

    @Override
    public SysUser findByUsername(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId, userId)
                .set(SysUser::getLastLoginTime, LocalDateTime.now());
        userMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public Set<String> getPermissionsByUserId(Long userId) {
        Set<String> permissions = new HashSet<>();

        // 1. 获取用户的角色ID列表
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return permissions;
        }

        // 2. 获取所有角色对应的菜单ID
        Set<Long> menuIds = new HashSet<>();
        for (Long roleId : roleIds) {
            List<Long> roleMenuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
            if (roleMenuIds != null) {
                menuIds.addAll(roleMenuIds);
            }
        }

        if (menuIds.isEmpty()) {
            return permissions;
        }

        // 3. 查询菜单并提取权限标识
        List<SysMenu> menus = menuMapper.selectBatchIds(menuIds);
        permissions = menus.stream()
                .map(SysMenu::getPermission)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        return permissions;
    }
}