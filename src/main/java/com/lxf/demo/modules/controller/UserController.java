package com.lxf.demo.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.dto.UserCreateRequest;
import com.lxf.demo.modules.dto.UserRoleAssignRequest;
import com.lxf.demo.modules.dto.UserUpdateRequest;
import com.lxf.demo.modules.entity.User;
import com.lxf.demo.modules.service.IMenuService;
import com.lxf.demo.modules.service.IRoleService;
import com.lxf.demo.modules.entity.Role;
import com.lxf.demo.modules.entity.Menu;
import com.lxf.demo.modules.service.IUserService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        User user = userService.getUserById(userDetails.getUserId());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Long> roleIds = userService.getUserRoleIds(user.getId());
        List<Role> roles = roleService.getRolesByUserId(user.getId());
        List<Menu> menus = menuService.getMenuTreeByRoleIds(roleIds);
        List<String> permissions = menus.stream()
                .filter(m -> m.getPermission() != null && !m.getPermission().isEmpty())
                .map(Menu::getPermission)
                .collect(Collectors.toList());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user);
        userInfo.put("roles", roles);
        userInfo.put("menus", menus);
        userInfo.put("permissions", permissions);

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setPassword(request.getPassword());
        user.setStatus(1); // 默认启用状态
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setStatus(request.getStatus());
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<IPage<User>> getAllUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<User> userPage = userService.getUserPage(pageNum, pageSize);
        return ResponseEntity.ok(userPage);
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<Void> assignRoles(@PathVariable Long id, @RequestBody UserRoleAssignRequest request) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.assignRoles(id, request.getRoleIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<List<Role>> getUserRoles(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<Role> roles = roleService.getRolesByUserId(id);
        return ResponseEntity.ok(roles);
    }
}