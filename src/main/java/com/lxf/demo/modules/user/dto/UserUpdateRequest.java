package com.lxf.demo.modules.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class UserUpdateRequest {

    @Size(min = 2, max = 50, message = "用户名长度应在2-50之间")
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    private Integer age;

    private Integer status;

    @Size(max = 50, message = "角色长度不能超过50")
    private String role;
}
