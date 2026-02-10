package com.lxf.demo.modules.dto;

import lombok.Data;

@Data
public class MenuUpdateRequest {

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
}
