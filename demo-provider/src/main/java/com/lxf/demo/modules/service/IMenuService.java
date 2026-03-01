package com.lxf.demo.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.entity.SysMenu;

import java.util.List;

public interface IMenuService {

    SysMenu saveMenu(SysMenu menu);

    SysMenu getMenuById(Long id);

    SysMenu updateMenu(SysMenu menu);

    boolean deleteMenu(Long id);

    List<SysMenu> getAllMenus();

    IPage<SysMenu> getMenuPage(int pageNum, int pageSize);

    List<SysMenu> getMenuTree();

    List<SysMenu> getMenusByRoleId(Long roleId);

    List<SysMenu> getMenuTreeByRoleIds(List<Long> roleIds);
}