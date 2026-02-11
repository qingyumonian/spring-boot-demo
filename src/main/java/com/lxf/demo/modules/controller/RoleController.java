package com.lxf.demo.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.dto.RoleCreateRequest;
import com.lxf.demo.modules.dto.RoleUpdateRequest;
import com.lxf.demo.modules.entity.SysRole;
import com.lxf.demo.modules.entity.SysMenu;
import com.lxf.demo.modules.service.IMenuService;
import com.lxf.demo.modules.service.IRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Resource
    private IRoleService roleService;

    @Resource
    private IMenuService menuService;

    @PostMapping
    public ResponseEntity<SysRole> createRole(@RequestBody RoleCreateRequest request) {
        SysRole role = new SysRole();
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setDescription(request.getDescription());
        role.setSortOrder(request.getSortOrder());
        SysRole savedRole = roleService.saveRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SysRole> getRoleById(@PathVariable Long id) {
        SysRole role = roleService.getRoleById(id);
        if (role != null) {
            return ResponseEntity.ok(role);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SysRole> updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        SysRole role = roleService.getRoleById(id);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setDescription(request.getDescription());
        role.setSortOrder(request.getSortOrder());
        SysRole updatedRole = roleService.updateRole(role);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        boolean deleted = roleService.deleteRole(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<IPage<SysRole>> getAllRoles(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<SysRole> rolePage = roleService.getRolePage(pageNum, pageSize);
        return ResponseEntity.ok(rolePage);
    }

    @PutMapping("/{id}/menus")
    public ResponseEntity<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        SysRole role = roleService.getRoleById(id);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        // roleService.assignMenus(id, menuIds); // 暂时注释，需要实现该方法
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/menus")
    public ResponseEntity<List<SysMenu>> getRoleMenus(@PathVariable Long id) {
        SysRole role = roleService.getRoleById(id);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        List<SysMenu> menus = menuService.getMenusByRoleId(id);
        return ResponseEntity.ok(menus);
    }
}