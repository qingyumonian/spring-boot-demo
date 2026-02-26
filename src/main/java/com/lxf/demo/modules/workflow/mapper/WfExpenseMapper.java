package com.lxf.demo.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxf.demo.modules.workflow.entity.WfExpense;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报销申请Mapper
 */
@Mapper
public interface WfExpenseMapper extends BaseMapper<WfExpense> {
}
