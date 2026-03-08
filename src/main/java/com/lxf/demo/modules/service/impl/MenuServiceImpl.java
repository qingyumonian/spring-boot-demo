package com.lxf.demo.modules.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxf.demo.modules.entity.SysMenu;
import com.lxf.demo.modules.mapper.MenuMapper;
import com.lxf.demo.modules.mapper.RoleMenuMapper;
import com.lxf.demo.modules.service.IMenuService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements IMenuService {

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public SysMenu saveMenu(SysMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getSortOrder() == null) {
            menu.setSortOrder(0);
        }
        menuMapper.insert(menu);
        return menu;
    }

    @DS("")
    @Override
    public SysMenu getMenuById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public SysMenu updateMenu(SysMenu menu) {
        menuMapper.updateById(menu);
        return menu;
    }

    @Override
    public boolean deleteMenu(Long id) {
        return menuMapper.deleteById(id) > 0;
    }

    @Override
    public List<SysMenu> getAllMenus() {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SysMenu::getSortOrder);
        return menuMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<SysMenu> getMenuPage(int pageNum, int pageSize) {
        Page<SysMenu> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SysMenu::getSortOrder);
        return menuMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<SysMenu> getMenuTree() {
        List<SysMenu> allMenus = getAllMenus();
        return buildTree(allMenus, 0L);
    }

    @Override
    public List<SysMenu> getMenusByRoleId(Long roleId) {
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
        if (menuIds == null || menuIds.isEmpty()) {
            return Collections.emptyList();
        }
        return menuMapper.selectBatchIds(menuIds);
    }

    @Override
    public List<SysMenu> getMenuTreeByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> allMenuIds = new ArrayList<>();
        for (Long roleId : roleIds) {
            List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
            if (menuIds != null) {
                allMenuIds.addAll(menuIds);
            }
        }

        if (allMenuIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> distinctMenuIds = allMenuIds.stream().distinct().collect(Collectors.toList());
        List<SysMenu> menus = menuMapper.selectBatchIds(distinctMenuIds);

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysMenu::getId, distinctMenuIds)
                   .orderByAsc(SysMenu::getSortOrder);
        menus = menuMapper.selectList(queryWrapper);

        return buildTree(menus, 0L);
    }

    private List<SysMenu> buildTree(List<SysMenu> menus, Long parentId) {
        Map<Long, List<SysMenu>> menusByParentId = menus.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        return buildTreeRecursive(menusByParentId, parentId);
    }

    private List<SysMenu> buildTreeRecursive(Map<Long, List<SysMenu>> menusByParentId, Long parentId) {
        List<SysMenu> children = menusByParentId.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }

        for (SysMenu menu : children) {
            menu.setChildren(buildTreeRecursive(menusByParentId, menu.getId()));
        }

        return children;
    }

    @Component
    public static class TokenProvider {

        public String generateToken() {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }
}