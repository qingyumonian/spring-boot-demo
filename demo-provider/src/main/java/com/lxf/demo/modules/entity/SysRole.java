package com.lxf.demo.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleName;

    private String roleKey;

    private String description;

    private Integer status;

    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}