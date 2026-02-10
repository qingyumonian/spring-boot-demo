package com.lxf.demo.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxf.demo.modules.entity.Menu;
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
    public Menu saveMenu(Menu menu) {
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

    @Override
    public Menu getMenuById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public Menu updateMenu(Menu menu) {
        menuMapper.updateById(menu);
        return menu;
    }

    @Override
    public boolean deleteMenu(Long id) {
        return menuMapper.deleteById(id) > 0;
    }

    @Override
    public List<Menu> getAllMenus() {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Menu::getSortOrder);
        return menuMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<Menu> getMenuPage(int pageNum, int pageSize) {
        Page<Menu> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Menu::getSortOrder);
        return menuMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = getAllMenus();
        return buildTree(allMenus, 0L);
    }

    @Override
    public List<Menu> getMenusByRoleId(Long roleId) {
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);
        if (menuIds == null || menuIds.isEmpty()) {
            return Collections.emptyList();
        }
        return menuMapper.selectBatchIds(menuIds);
    }

    @Override
    public List<Menu> getMenuTreeByRoleIds(List<Long> roleIds) {
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
        List<Menu> menus = menuMapper.selectBatchIds(distinctMenuIds);

        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Menu::getId, distinctMenuIds)
                   .orderByAsc(Menu::getSortOrder);
        menus = menuMapper.selectList(queryWrapper);

        return buildTree(menus, 0L);
    }

    private List<Menu> buildTree(List<Menu> menus, Long parentId) {
        Map<Long, List<Menu>> menusByParentId = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));

        return buildTreeRecursive(menusByParentId, parentId);
    }

    private List<Menu> buildTreeRecursive(Map<Long, List<Menu>> menusByParentId, Long parentId) {
        List<Menu> children = menusByParentId.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }

        for (Menu menu : children) {
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