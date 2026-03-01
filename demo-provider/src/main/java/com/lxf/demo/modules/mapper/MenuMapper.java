package com.lxf.demo.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxf.demo.modules.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MenuMapper extends BaseMapper<SysMenu> {
}
