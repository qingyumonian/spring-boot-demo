package com.lxf.demo.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private Integer age;
    private Integer status;
    private String role;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
