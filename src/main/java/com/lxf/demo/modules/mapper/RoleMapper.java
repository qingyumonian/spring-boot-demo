package com.lxf.demo.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxf.demo.modules.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT * FROM `role` WHERE role_key = #{roleKey}")
    Role selectByRoleKey(String roleKey);
}
