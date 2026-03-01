package com.lxf.demo.consumer.controller;

import com.lxf.demo.api.common.R;
import com.lxf.demo.api.dto.PageResult;
import com.lxf.demo.api.dto.UserDTO;
import com.lxf.demo.api.service.DubboUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/remote/users")
public class RemoteUserController {

    @DubboReference
    private DubboUserService userService;

    @GetMapping("/{id}")
    public R<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return R.fail(404, "用户不存在");
        }
        return R.ok(user);
    }

    @GetMapping("/username/{username}")
    public R<UserDTO> findByUsername(@PathVariable String username) {
        UserDTO user = userService.findByUsername(username);
        if (user == null) {
            return R.fail(404, "用户不存在");
        }
        return R.ok(user);
    }

    @GetMapping
    public R<PageResult<UserDTO>> getUserPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(userService.getUserPage(pageNum, pageSize));
    }

    @GetMapping("/all")
    public R<List<UserDTO>> getAllUsers() {
        return R.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}/permissions")
    public R<Set<String>> getPermissions(@PathVariable Long id) {
        return R.ok(userService.getPermissionsByUserId(id));
    }

    @GetMapping("/{id}/roles")
    public R<List<Long>> getUserRoleIds(@PathVariable Long id) {
        return R.ok(userService.getUserRoleIds(id));
    }
}
