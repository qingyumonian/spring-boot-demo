package com.lxf.demo.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.entity.Role;
import com.lxf.demo.security.userdetails.CustomUserDetails;

import java.util.List;

public interface IRoleService {

    Role saveRole(Role role);

    Role getRoleById(Long id);

    Role updateRole(Role role);

    boolean deleteRole(Long id);

    List<Role> getAllRoles();

    IPage<Role> getRolePage(int pageNum, int pageSize);

    Role findByRoleKey(String roleKey);

    List<Role> getRolesByUserId(Long userId);

    interface TokenService {

        String saveAccessToken(CustomUserDetails userDetails);

        CustomUserDetails getUserByToken(String token);

        void removeAccessToken(String token);

        boolean validateToken(String token);
    }
}