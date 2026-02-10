package com.lxf.demo.modules.dto;

import com.lxf.demo.modules.entity.Menu;
import com.lxf.demo.modules.entity.Role;
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
    private List<Role> roles;
    private List<Menu> menus;
    private List<String> permissions;
}
