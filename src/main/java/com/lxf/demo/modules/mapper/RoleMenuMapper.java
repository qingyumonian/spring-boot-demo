package com.lxf.demo.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxf.demo.modules.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Select("SELECT menu_id FROM role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(Long roleId);
}
