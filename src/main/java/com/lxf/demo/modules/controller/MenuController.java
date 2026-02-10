package com.lxf.demo.modules.controller;

import com.lxf.demo.modules.dto.MenuCreateRequest;
import com.lxf.demo.modules.dto.MenuUpdateRequest;
import com.lxf.demo.modules.entity.Menu;
import com.lxf.demo.modules.service.IMenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Resource
    private IMenuService menuService;

    @PostMapping
    public ResponseEntity<Menu> createMenu(@RequestBody MenuCreateRequest request) {
        Menu menu = new Menu();
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
        Menu savedMenu = menuService.saveMenu(menu);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMenu);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenuById(@PathVariable Long id) {
        Menu menu = menuService.getMenuById(id);
        if (menu != null) {
            return ResponseEntity.ok(menu);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Menu>> getAllMenus() {
        List<Menu> menus = menuService.getAllMenus();
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<Menu>> getMenuTree() {
        List<Menu> menuTree = menuService.getMenuTree();
        return ResponseEntity.ok(menuTree);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(@PathVariable Long id, @RequestBody MenuUpdateRequest request) {
        Menu menu = menuService.getMenuById(id);
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
        Menu updatedMenu = menuService.updateMenu(menu);
        return ResponseEntity.ok(updatedMenu);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        boolean deleted = menuService.deleteMenu(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}