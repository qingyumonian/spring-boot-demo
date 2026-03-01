package com.lxf.demo.modules.dto;

import com.lxf.demo.modules.entity.SysMenu;
import com.lxf.demo.modules.entity.SysRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private Long expiresIn;
    private List<SysRole> roles;
    private List<SysMenu> menus;
    private List<String> permissions;
}
