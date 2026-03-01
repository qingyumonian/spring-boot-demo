package com.lxf.demo.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.dto.RoleCreateRequest;
import com.lxf.demo.modules.dto.RoleUpdateRequest;
import com.lxf.demo.modules.entity.SysRole;
import com.lxf.demo.modules.entity.SysMenu;
import com.lxf.demo.modules.service.IMenuService;
import com.lxf.demo.modules.service.IRoleService;
import com.lxf.demo.common.result.R;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('system:role:add')")
    public R<SysRole> createRole(@RequestBody RoleCreateRequest request) {
        SysRole role = new SysRole();
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setDescription(request.getDescription());
        role.setSortOrder(request.getSortOrder());
        SysRole savedRole = roleService.saveRole(role);
        return R.ok(savedRole);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public R<SysRole> getRoleById(@PathVariable Long id) {
        SysRole role = roleService.getRoleById(id);
        if (role != null) {
            return R.ok(role);
        } else {
            return R.fail(404, "角色不存在");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public R<SysRole> updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        SysRole role = roleService.getRoleById(id);
        if (role == null) {
            return R.fail(404, "角色不存在");
        }
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setDescription(request.getDescription());
        role.setSortOrder(request.getSortOrder());
        SysRole updatedRole = roleService.updateRole(role);
        return R.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public R<Void> deleteRole(@PathVariable Long id) {
        boolean deleted = roleService.deleteRole(id);
        if (deleted) {
            return R.ok();
        } else {
            return R.fail(404, "角色不存在");
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:query')")
    public R<IPage<SysRole>> getAllRoles(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<SysRole> rolePage = roleService.getRolePage(pageNum, pageSize);
        return R.ok(rolePage);
    }

    @PutMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public R<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        SysRole role = roleService.getRoleById(id);
        if (role == null) {
            return R.fail(404, "角色不存在");
        }
        // roleService.assignMenus(id, menuIds); // 暂时注释，需要实现该方法
        return R.ok();
    }

    @GetMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:query')")
    public R<List<SysMenu>> getRoleMenus(@PathVariable Long id) {
        SysRole role = roleService.getRoleById(id);
        if (role == null) {
            return R.fail(404, "角色不存在");
        }
        List<SysMenu> menus = menuService.getMenusByRoleId(id);
        return R.ok(menus);
    }
}