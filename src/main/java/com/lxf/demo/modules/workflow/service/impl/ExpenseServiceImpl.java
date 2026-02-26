package com.lxf.demo.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxf.demo.common.exception.BusinessException;
import com.lxf.demo.modules.workflow.dto.ExpenseApplyRequest;
import com.lxf.demo.modules.workflow.dto.ExpenseVO;
import com.lxf.demo.modules.workflow.dto.ProcessStartRequest;
import com.lxf.demo.modules.workflow.entity.WfExpense;
import com.lxf.demo.modules.workflow.entity.WfFormInstance;
import com.lxf.demo.modules.workflow.mapper.WfExpenseMapper;
import com.lxf.demo.modules.workflow.mapper.WfFormInstanceMapper;
import com.lxf.demo.modules.workflow.service.IExpenseService;
import com.lxf.demo.modules.workflow.service.IProcessInstanceService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 报销服务实现
 */
@Service
public class ExpenseServiceImpl implements IExpenseService {

    private static final String FORM_TYPE = "expense";
    private static final String PROCESS_KEY = "expense-approval";
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    @Resource
    private WfExpenseMapper expenseMapper;

    @Resource
    private WfFormInstanceMapper formInstanceMapper;

    @Resource
    private IProcessInstanceService processInstanceService;

    @Resource
    private RuntimeService runtimeService;

    @Override
    @Transactional
    public WfExpense create(ExpenseApplyRequest request) {
        CustomUserDetails user = getCurrentUser();

        WfExpense expense = new WfExpense();
        expense.setExpenseNo(generateExpenseNo());
        expense.setExpenseType(request.getExpenseType());
        expense.setAmount(request.getAmount());
        expense.setReason(request.getReason());
        expense.setApplicantId(user.getId());
        expense.setApplicantName(user.getUsername());
        expense.setStatus(0); // 草稿
        expense.setAttachments(request.getAttachments());
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());

        expenseMapper.insert(expense);
        return expense;
    }

    @Override
    @Transactional
    public void submit(Long expenseId) {
        WfExpense expense = expenseMapper.selectById(expenseId);
        if (expense == null) {
            throw new BusinessException("报销申请不存在");
        }

        CustomUserDetails user = getCurrentUser();
        if (!expense.getApplicantId().equals(user.getId())) {
            throw new BusinessException("只能提交自己的报销申请");
        }

        if (expense.getStatus() != 0 && expense.getStatus() != 3) {
            throw new BusinessException("当前状态不允许提交");
        }

        // 发起流程
        ProcessStartRequest startRequest = new ProcessStartRequest();
        startRequest.setProcessDefinitionKey(PROCESS_KEY);
        startRequest.setFormType(FORM_TYPE);
        startRequest.setFormId(expenseId);
        startRequest.setTitle(expense.getApplicantName() + "的报销申请-" + expense.getExpenseNo());

        // 设置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", expense.getAmount());
        variables.put("expenseType", expense.getExpenseType());
        startRequest.setVariables(variables);

        processInstanceService.start(startRequest);

        // 更新报销状态
        expense.setStatus(1); // 审批中
        expense.setApplyTime(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        expenseMapper.updateById(expense);
    }

    @Override
    public ExpenseVO getById(Long id) {
        WfExpense expense = expenseMapper.selectById(id);
        if (expense == null) {
            throw new BusinessException("报销申请不存在");
        }
        return convertToVO(expense);
    }

    @Override
    public IPage<ExpenseVO> listMyExpenses(int pageNum, int pageSize) {
        CustomUserDetails user = getCurrentUser();

        LambdaQueryWrapper<WfExpense> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfExpense::getApplicantId, user.getId());
        wrapper.orderByDesc(WfExpense::getCreatedAt);

        Page<WfExpense> page = new Page<>(pageNum, pageSize);
        IPage<WfExpense> expensePage = expenseMapper.selectPage(page, wrapper);

        return expensePage.convert(this::convertToVO);
    }

    @Override
    @Transactional
    public void updateStatus(Long expenseId, Integer status) {
        WfExpense expense = expenseMapper.selectById(expenseId);
        if (expense == null) {
            return;
        }
        expense.setStatus(status);
        expense.setUpdatedAt(LocalDateTime.now());
        expenseMapper.updateById(expense);
    }

    private ExpenseVO convertToVO(WfExpense expense) {
        ExpenseVO vo = new ExpenseVO();
        vo.setId(expense.getId());
        vo.setExpenseNo(expense.getExpenseNo());
        vo.setExpenseType(expense.getExpenseType());
        vo.setAmount(expense.getAmount());
        vo.setReason(expense.getReason());
        vo.setApplicantId(expense.getApplicantId());
        vo.setApplicantName(expense.getApplicantName());
        vo.setApplyTime(expense.getApplyTime());
        vo.setStatus(expense.getStatus());
        vo.setStatusName(getStatusName(expense.getStatus()));
        vo.setAttachments(expense.getAttachments());
        vo.setCreatedAt(expense.getCreatedAt());

        // 获取流程实例信息
        LambdaQueryWrapper<WfFormInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfFormInstance::getFormType, FORM_TYPE);
        wrapper.eq(WfFormInstance::getFormId, expense.getId());
        WfFormInstance formInstance = formInstanceMapper.selectOne(wrapper);
        if (formInstance != null) {
            vo.setProcessInstanceId(formInstance.getProcessInstanceId());

            // 获取当前节点名称
            if (formInstance.getProcessInstanceId() != null && expense.getStatus() == 1) {
                try {
                    List<Execution> executions = runtimeService.createExecutionQuery()
                            .processInstanceId(formInstance.getProcessInstanceId())
                            .onlyChildExecutions()
                            .list();
                    if (!executions.isEmpty()) {
                        vo.setCurrentActivityName(executions.get(0).getActivityId());
                    }
                } catch (Exception e) {
                    // 流程可能已结束
                }
            }
        }

        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "草稿";
            case 1: return "审批中";
            case 2: return "已通过";
            case 3: return "已拒绝";
            case 4: return "已撤销";
            default: return "未知";
        }
    }

    private String generateExpenseNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "EXP" + date + String.format("%04d", SEQUENCE.incrementAndGet() % 10000);
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new BusinessException("请先登录");
        }
        return (CustomUserDetails) auth.getPrincipal();
    }
}
