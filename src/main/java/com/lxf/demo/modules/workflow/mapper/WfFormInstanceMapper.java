package com.lxf.demo.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxf.demo.modules.workflow.entity.WfFormInstance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表单-流程关联Mapper
 */
@Mapper
public interface WfFormInstanceMapper extends BaseMapper<WfFormInstance> {
}
