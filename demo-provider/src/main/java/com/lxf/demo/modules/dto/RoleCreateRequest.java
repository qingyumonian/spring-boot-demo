package com.lxf.demo.modules.dto;

import lombok.Data;

@Data
public class RoleCreateRequest {

    private String roleName;

    private String roleKey;

    private String description;

    private Integer sortOrder;
}
