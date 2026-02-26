package com.lxf.demo.modules.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.common.result.R;
import com.lxf.demo.modules.workflow.dto.ExpenseApplyRequest;
import com.lxf.demo.modules.workflow.dto.ExpenseVO;
import com.lxf.demo.modules.workflow.entity.WfExpense;
import com.lxf.demo.modules.workflow.service.IExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 报销申请控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/workflow/expenses")
public class ExpenseController {

    @Resource
    private IExpenseService expenseService;

    /**
     * 创建报销申请（草稿）
     */
    @PostMapping
    public R<WfExpense> create(@Valid @RequestBody ExpenseApplyRequest request) {
        WfExpense expense = expenseService.create(request);
        return R.ok(expense);
    }

    /**
     * 提交报销申请（发起流程）
     */
    @PostMapping("/{id}/submit")
    public R<Void> submit(@PathVariable Long id) {
        expenseService.submit(id);
        return R.ok();
    }

    /**
     * 获取报销详情
     */
    @GetMapping("/{id}")
    public R<ExpenseVO> getById(@PathVariable Long id) {
        ExpenseVO expense = expenseService.getById(id);
        return R.ok(expense);
    }

    /**
     * 分页查询我的报销
     */
    @GetMapping("/my")
    public R<IPage<ExpenseVO>> listMyExpenses(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<ExpenseVO> result = expenseService.listMyExpenses(pageNum, pageSize);
        return R.ok(result);
    }
}
