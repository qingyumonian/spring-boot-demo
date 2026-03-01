package com.lxf.demo.consumer.controller;

import com.lxf.demo.api.common.R;
import com.lxf.demo.api.dto.MenuDTO;
import com.lxf.demo.api.dto.PageResult;
import com.lxf.demo.api.service.DubboMenuService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/remote/menus")
public class RemoteMenuController {

    @DubboReference
    private DubboMenuService menuService;

    @GetMapping("/{id}")
    public R<MenuDTO> getMenuById(@PathVariable Long id) {
        MenuDTO menu = menuService.getMenuById(id);
        if (menu == null) {
            return R.fail(404, "菜单不存在");
        }
        return R.ok(menu);
    }

    @GetMapping
    public R<PageResult<MenuDTO>> getMenuPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(menuService.getMenuPage(pageNum, pageSize));
    }

    @GetMapping("/all")
    public R<List<MenuDTO>> getAllMenus() {
        return R.ok(menuService.getAllMenus());
    }

    @GetMapping("/tree")
    public R<List<MenuDTO>> getMenuTree() {
        return R.ok(menuService.getMenuTree());
    }

    @GetMapping("/role/{roleId}")
    public R<List<MenuDTO>> getMenusByRoleId(@PathVariable Long roleId) {
        return R.ok(menuService.getMenusByRoleId(roleId));
    }

    @PostMapping("/tree/roles")
    public R<List<MenuDTO>> getMenuTreeByRoleIds(@RequestBody List<Long> roleIds) {
        return R.ok(menuService.getMenuTreeByRoleIds(roleIds));
    }
}
