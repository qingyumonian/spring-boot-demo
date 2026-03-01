package com.lxf.demo.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MenuDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private List<MenuDTO> children;
}
