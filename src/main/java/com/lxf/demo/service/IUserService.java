package com.lxf.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.entity.User;

import java.util.List;

public interface IUserService {

    User saveUser(User user);

    User getUserById(Long id);

    User updateUser(User user);

    boolean deleteUser(Long id);

    List<User> getAllUsers();

    IPage<User> getUserPage(int pageNum, int pageSize);
}
