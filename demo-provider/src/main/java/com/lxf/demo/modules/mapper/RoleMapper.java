package com.lxf.demo.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxf.demo.modules.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {

    @Select("SELECT * FROM sys_role WHERE role_key = #{roleKey}")
    SysRole selectByRoleKey(String roleKey);
}
