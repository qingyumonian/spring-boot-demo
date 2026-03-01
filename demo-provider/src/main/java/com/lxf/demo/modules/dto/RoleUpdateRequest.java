package com.lxf.demo.modules.dto;

import lombok.Data;

@Data
public class RoleUpdateRequest {

    private String roleName;

    private String roleKey;

    private String description;

    private Integer status;

    private Integer sortOrder;
}
