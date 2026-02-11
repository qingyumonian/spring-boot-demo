package com.lxf.demo.modules.controller;

import com.lxf.demo.modules.dto.MenuCreateRequest;
import com.lxf.demo.modules.dto.MenuUpdateRequest;
import com.lxf.demo.modules.entity.SysMenu;
import com.lxf.demo.modules.service.IMenuService;
import com.lxf.demo.common.result.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Resource
    private IMenuService menuService;

    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public R<SysMenu> createMenu(@RequestBody MenuCreateRequest request) {
        SysMenu menu = new SysMenu();
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setMenuType(request.getMenuType());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPermission(request.getPermission());
        menu.setIcon(request.getIcon());
        menu.setVisible(1); // 默认可见
        menu.setStatus(1); // 默认启用
        menu.setSortOrder(request.getSortOrder());
        SysMenu savedMenu = menuService.saveMenu(menu);
        return R.ok(savedMenu);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public R<SysMenu> getMenuById(@PathVariable Long id) {
        SysMenu menu = menuService.getMenuById(id);
        if (menu != null) {
            return R.ok(menu);
        } else {
            return R.fail(404, "菜单不存在");
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:menu:query')")
    public R<List<SysMenu>> getAllMenus() {
        List<SysMenu> menus = menuService.getAllMenus();
        return R.ok(menus);
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public R<List<SysMenu>> getMenuTree() {
        List<SysMenu> menuTree = menuService.getMenuTree();
        return R.ok(menuTree);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public R<SysMenu> updateMenu(@PathVariable Long id, @RequestBody MenuUpdateRequest request) {
        SysMenu menu = menuService.getMenuById(id);
        if (menu == null) {
            return R.fail(404, "菜单不存在");
        }
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setMenuType(request.getMenuType());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setPermission(request.getPermission());
        menu.setIcon(request.getIcon());
        menu.setVisible(1); // 默认可见
        menu.setStatus(1); // 默认启用
        menu.setSortOrder(request.getSortOrder());
        SysMenu updatedMenu = menuService.updateMenu(menu);
        return R.ok(updatedMenu);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public R<Void> deleteMenu(@PathVariable Long id) {
        boolean deleted = menuService.deleteMenu(id);
        if (deleted) {
            return R.ok();
        } else {
            return R.fail(404, "菜单不存在");
        }
    }
}