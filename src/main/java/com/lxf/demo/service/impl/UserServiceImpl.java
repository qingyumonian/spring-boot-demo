package com.lxf.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxf.demo.entity.User;
import com.lxf.demo.mapper.UserMapper;
import com.lxf.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User saveUser(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User updateUser(User user) {
        userMapper.updateById(user);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    @Override
    public IPage<User> getUserPage(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return userMapper.selectPage(page, null);
    }
}
