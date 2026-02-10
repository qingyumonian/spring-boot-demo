package com.lxf.demo.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.entity.Menu;

import java.util.List;

public interface IMenuService {

    Menu saveMenu(Menu menu);

    Menu getMenuById(Long id);

    Menu updateMenu(Menu menu);

    boolean deleteMenu(Long id);

    List<Menu> getAllMenus();

    IPage<Menu> getMenuPage(int pageNum, int pageSize);

    List<Menu> getMenuTree();

    List<Menu> getMenusByRoleId(Long roleId);

    List<Menu> getMenuTreeByRoleIds(List<Long> roleIds);
}