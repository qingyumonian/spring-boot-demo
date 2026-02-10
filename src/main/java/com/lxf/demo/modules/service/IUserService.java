package com.lxf.demo.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.entity.User;

import java.util.List;

public interface IUserService {

    User saveUser(User user);

    User getUserById(Long id);

    User updateUser(User user);

    boolean deleteUser(Long id);

    List<User> getAllUsers();

    IPage<User> getUserPage(int pageNum, int pageSize);

    User findByUsername(String username);

    void updateLastLoginTime(Long userId);

    void assignRoles(Long userId, List<Long> roleIds);

    List<Long> getUserRoleIds(Long userId);
}