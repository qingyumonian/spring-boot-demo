package com.lxf.demo.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.dto.RoleCreateRequest;
import com.lxf.demo.modules.dto.RoleUpdateRequest;
import com.lxf.demo.modules.entity.Role;
import com.lxf.demo.modules.entity.Menu;
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
    public ResponseEntity<Role> createRole(@RequestBody RoleCreateRequest request) {
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setDescription(request.getDescription());
        role.setSortOrder(request.getSortOrder());
        Role savedRole = roleService.saveRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        if (role != null) {
            return ResponseEntity.ok(role);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setDescription(request.getDescription());
        role.setSortOrder(request.getSortOrder());
        Role updatedRole = roleService.updateRole(role);
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
    public ResponseEntity<IPage<Role>> getAllRoles(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<Role> rolePage = roleService.getRolePage(pageNum, pageSize);
        return ResponseEntity.ok(rolePage);
    }

    @PutMapping("/{id}/menus")
    public ResponseEntity<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        // roleService.assignMenus(id, menuIds); // 暂时注释，需要实现该方法
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/menus")
    public ResponseEntity<List<Menu>> getRoleMenus(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        List<Menu> menus = menuService.getMenusByRoleId(id);
        return ResponseEntity.ok(menus);
    }
}