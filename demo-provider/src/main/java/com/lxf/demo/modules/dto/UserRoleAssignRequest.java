package com.lxf.demo.modules.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleAssignRequest {
    private List<Long> roleIds;
}
