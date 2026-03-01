package com.lxf.demo.api.service;

import com.lxf.demo.api.dto.PageResult;
import com.lxf.demo.api.dto.UserDTO;

import java.util.List;
import java.util.Set;

public interface DubboUserService {

    UserDTO getUserById(Long id);

    UserDTO findByUsername(String username);

    PageResult<UserDTO> getUserPage(int pageNum, int pageSize);

    List<UserDTO> getAllUsers();

    Set<String> getPermissionsByUserId(Long userId);

    List<Long> getUserRoleIds(Long userId);
}
