package com.lxf.demo.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RoleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String roleName;
    private String roleKey;
    private String description;
    private Integer status;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
