package com.lxf.demo.api.service;

import com.lxf.demo.api.dto.PageResult;
import com.lxf.demo.api.dto.RoleDTO;

import java.util.List;

public interface DubboRoleService {

    RoleDTO getRoleById(Long id);

    RoleDTO findByRoleKey(String roleKey);

    PageResult<RoleDTO> getRolePage(int pageNum, int pageSize);

    List<RoleDTO> getAllRoles();

    List<RoleDTO> getRolesByUserId(Long userId);
}
