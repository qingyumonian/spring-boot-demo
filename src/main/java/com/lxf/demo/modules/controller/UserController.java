package com.lxf.demo.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.dto.UserCreateRequest;
import com.lxf.demo.modules.dto.UserRoleAssignRequest;
import com.lxf.demo.modules.dto.UserUpdateRequest;
import com.lxf.demo.modules.entity.*;
import com.lxf.demo.modules.service.IMenuService;
import com.lxf.demo.modules.service.IRoleService;
import com.lxf.demo.modules.service.IUserService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IRoleService roleService;

    @Resource
    private IMenuService menuService;

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        SysUser user = userService.getUserById(userDetails.getId());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Long> roleIds = userService.getUserRoleIds(user.getId());
        List<SysRole> roles = roleService.getRolesByUserId(user.getId());
        List<SysMenu> menus = menuService.getMenuTreeByRoleIds(roleIds);
        List<String> permissions = menus.stream()
                .filter(m -> m.getPermission() != null && !m.getPermission().isEmpty())
                .map(SysMenu::getPermission)
                .collect(Collectors.toList());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user);
        userInfo.put("roles", roles);
        userInfo.put("menus", menus);
        userInfo.put("permissions", permissions);

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public ResponseEntity<SysUser> createUser(@RequestBody UserCreateRequest request) {
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setPassword(request.getPassword());
        user.setStatus(1); // 默认启用状态
        SysUser savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public ResponseEntity<SysUser> getUserById(@PathVariable Long id) {
        SysUser user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public ResponseEntity<SysUser> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        SysUser user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setStatus(request.getStatus());
        SysUser updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user:list')")
    public ResponseEntity<IPage<SysUser>> getAllUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<SysUser> userPage = userService.getUserPage(pageNum, pageSize);
        return ResponseEntity.ok(userPage);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public ResponseEntity<Void> assignRoles(@PathVariable Long id, @RequestBody UserRoleAssignRequest request) {
        SysUser user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.assignRoles(id, request.getRoleIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:query')")
    public ResponseEntity<List<SysRole>> getUserRoles(@PathVariable Long id) {
        SysUser user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<SysRole> roles = roleService.getRolesByUserId(id);
        return ResponseEntity.ok(roles);
    }
}