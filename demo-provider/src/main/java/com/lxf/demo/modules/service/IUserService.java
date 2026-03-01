package com.lxf.demo.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.entity.SysUser;

import java.util.List;
import java.util.Set;

public interface IUserService {

    SysUser saveUser(SysUser user);

    SysUser getUserById(Long id);

    SysUser updateUser(SysUser user);

    boolean deleteUser(Long id);

    List<SysUser> getAllUsers();

    IPage<SysUser> getUserPage(int pageNum, int pageSize);

    SysUser findByUsername(String username);

    void updateLastLoginTime(Long userId);

    void assignRoles(Long userId, List<Long> roleIds);

    List<Long> getUserRoleIds(Long userId);

    /**
     * 获取用户的权限标识列表
     */
    Set<String> getPermissionsByUserId(Long userId);
}