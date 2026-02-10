package com.lxf.demo.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxf.demo.modules.entity.Role;
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
    public Role saveRole(Role role) {
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
    public Role getRoleById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Role updateRole(Role role) {
        roleMapper.updateById(role);
        return role;
    }

    @Override
    public boolean deleteRole(Long id) {
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    public List<Role> getAllRoles() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Role::getSortOrder);
        return roleMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<Role> getRolePage(int pageNum, int pageSize) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Role::getSortOrder);
        return roleMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Role findByRoleKey(String roleKey) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleKey, roleKey);
        return roleMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return roleMapper.selectBatchIds(roleIds);
    }
}