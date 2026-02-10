package com.lxf.demo.modules.user.dto;

import lombok.Data;


@Data
public class UserUpdateRequest {

    private String username;

    private String email;

    private Integer age;

    private Integer status;

    private String role;
}
