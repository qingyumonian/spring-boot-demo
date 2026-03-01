package com.lxf.demo.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String email;

    private Integer age;

    private String password;

    private Integer status;

    private String role;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}