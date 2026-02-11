package com.lxf.demo.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.entity.SysRole;
import com.lxf.demo.security.userdetails.CustomUserDetails;

import java.util.List;

public interface IRoleService {

    SysRole saveRole(SysRole role);

    SysRole getRoleById(Long id);

    SysRole updateRole(SysRole role);

    boolean deleteRole(Long id);

    List<SysRole> getAllRoles();

    IPage<SysRole> getRolePage(int pageNum, int pageSize);

    SysRole findByRoleKey(String roleKey);

    List<SysRole> getRolesByUserId(Long userId);


}