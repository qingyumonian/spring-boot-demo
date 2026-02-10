package com.lxf.demo.modules.dto;

import lombok.Data;

@Data
public class MenuCreateRequest {

    private Long parentId;

    private String menuName;

    private String menuType;

    private String path;

    private String component;

    private String permission;

    private String icon;

    private Integer sortOrder;
}
