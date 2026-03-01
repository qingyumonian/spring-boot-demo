package com.lxf.demo.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxf.demo.modules.entity.SysRole;
import com.lxf.demo.modules.mapper.RoleMapper;
import com.lxf.demo.modules.mapper.UserRoleMapper;
import com.lxf.demo.modules.service.IRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class RoleServiceImpl implements IRoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public SysRole saveRole(SysRole role) {
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        if (role.getSortOrder() == null) {
            role.setSortOrder(0);
        }
        roleMapper.insert(role);
        return role;
    }

    @Override
    public SysRole getRoleById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public SysRole updateRole(SysRole role) {
        roleMapper.updateById(role);
        return role;
    }

    @Override
    public boolean deleteRole(Long id) {
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    public List<SysRole> getAllRoles() {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SysRole::getSortOrder);
        return roleMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<SysRole> getRolePage(int pageNum, int pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SysRole::getSortOrder);
        return roleMapper.selectPage(page, queryWrapper);
    }

    @Override
    public SysRole findByRoleKey(String roleKey) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleKey, roleKey);
        return roleMapper.selectOne(queryWrapper);
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return roleMapper.selectBatchIds(roleIds);
    }
}