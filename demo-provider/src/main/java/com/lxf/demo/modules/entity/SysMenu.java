package com.lxf.demo.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String menuName;

    private String menuType;

    private String path;

    private String component;

    private String permission;

    private String icon;

    private Integer visible;

    private Integer status;

    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<SysMenu> children;
}