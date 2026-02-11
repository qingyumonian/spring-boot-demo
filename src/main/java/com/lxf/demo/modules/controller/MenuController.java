package com.lxf.demo.modules.controller;

import com.lxf.demo.modules.dto.MenuCreateRequest;
import com.lxf.demo.modules.dto.MenuUpdateRequest;
import com.lxf.demo.modules.entity.SysMenu;
import com.lxf.demo.modules.service.IMenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SysMenu> createMenu(@RequestBody MenuCreateRequest request) {
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
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMenu);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public ResponseEntity<SysMenu> getMenuById(@PathVariable Long id) {
        SysMenu menu = menuService.getMenuById(id);
        if (menu != null) {
            return ResponseEntity.ok(menu);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:menu:query')")
    public ResponseEntity<List<SysMenu>> getAllMenus() {
        List<SysMenu> menus = menuService.getAllMenus();
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public ResponseEntity<List<SysMenu>> getMenuTree() {
        List<SysMenu> menuTree = menuService.getMenuTree();
        return ResponseEntity.ok(menuTree);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public ResponseEntity<SysMenu> updateMenu(@PathVariable Long id, @RequestBody MenuUpdateRequest request) {
        SysMenu menu = menuService.getMenuById(id);
        if (menu == null) {
            return ResponseEntity.notFound().build();
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
        return ResponseEntity.ok(updatedMenu);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        boolean deleted = menuService.deleteMenu(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}