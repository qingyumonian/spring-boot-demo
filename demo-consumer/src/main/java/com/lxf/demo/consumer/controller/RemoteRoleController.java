package com.lxf.demo.consumer.controller;

import com.lxf.demo.api.common.R;
import com.lxf.demo.api.dto.PageResult;
import com.lxf.demo.api.dto.RoleDTO;
import com.lxf.demo.api.service.DubboRoleService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/remote/roles")
public class RemoteRoleController {

    @DubboReference
    private DubboRoleService roleService;

    @GetMapping("/{id}")
    public R<RoleDTO> getRoleById(@PathVariable Long id) {
        RoleDTO role = roleService.getRoleById(id);
        if (role == null) {
            return R.fail(404, "角色不存在");
        }
        return R.ok(role);
    }

    @GetMapping("/key/{roleKey}")
    public R<RoleDTO> findByRoleKey(@PathVariable String roleKey) {
        RoleDTO role = roleService.findByRoleKey(roleKey);
        if (role == null) {
            return R.fail(404, "角色不存在");
        }
        return R.ok(role);
    }

    @GetMapping
    public R<PageResult<RoleDTO>> getRolePage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(roleService.getRolePage(pageNum, pageSize));
    }

    @GetMapping("/all")
    public R<List<RoleDTO>> getAllRoles() {
        return R.ok(roleService.getAllRoles());
    }

    @GetMapping("/user/{userId}")
    public R<List<RoleDTO>> getRolesByUserId(@PathVariable Long userId) {
        return R.ok(roleService.getRolesByUserId(userId));
    }
}
