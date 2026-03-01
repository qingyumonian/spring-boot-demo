package com.lxf.demo.api.service;

import com.lxf.demo.api.dto.MenuDTO;
import com.lxf.demo.api.dto.PageResult;

import java.util.List;

public interface DubboMenuService {

    MenuDTO getMenuById(Long id);

    PageResult<MenuDTO> getMenuPage(int pageNum, int pageSize);

    List<MenuDTO> getAllMenus();

    List<MenuDTO> getMenuTree();

    List<MenuDTO> getMenusByRoleId(Long roleId);

    List<MenuDTO> getMenuTreeByRoleIds(List<Long> roleIds);
}
