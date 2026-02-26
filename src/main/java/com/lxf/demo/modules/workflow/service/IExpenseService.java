package com.lxf.demo.modules.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.modules.workflow.dto.ExpenseApplyRequest;
import com.lxf.demo.modules.workflow.dto.ExpenseVO;
import com.lxf.demo.modules.workflow.entity.WfExpense;

/**
 * 报销服务
 */
public interface IExpenseService {

    /**
     * 创建报销申请（草稿）
     */
    WfExpense create(ExpenseApplyRequest request);

    /**
     * 提交报销申请（发起流程）
     */
    void submit(Long expenseId);

    /**
     * 获取报销详情
     */
    ExpenseVO getById(Long id);

    /**
     * 分页查询我的报销
     */
    IPage<ExpenseVO> listMyExpenses(int pageNum, int pageSize);

    /**
     * 更新报销状态
     */
    void updateStatus(Long expenseId, Integer status);
}
