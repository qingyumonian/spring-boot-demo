package com.lxf.demo.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxf.demo.modules.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User selectByUsername(String username);
}
