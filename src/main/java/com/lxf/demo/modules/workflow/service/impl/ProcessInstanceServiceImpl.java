package com.lxf.demo.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxf.demo.common.exception.BusinessException;
import com.lxf.demo.modules.workflow.dto.ProcessInstanceVO;
import com.lxf.demo.modules.workflow.dto.ProcessStartRequest;
import com.lxf.demo.modules.workflow.entity.WfFormInstance;
import com.lxf.demo.modules.workflow.mapper.WfFormInstanceMapper;
import com.lxf.demo.modules.workflow.service.IProcessInstanceService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程实例服务实现
 */
@Service
public class ProcessInstanceServiceImpl implements IProcessInstanceService {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private WfFormInstanceMapper formInstanceMapper;

    @Override
    @Transactional
    public ProcessInstanceVO start(ProcessStartRequest request) {
        CustomUserDetails user = getCurrentUser();

        // 构建流程变量
        Map<String, Object> variables = new HashMap<>();
        if (request.getVariables() != null) {
            variables.putAll(request.getVariables());
        }
        variables.put("initiator", user.getId().toString());
        variables.put("initiatorName", user.getUsername());

        // 构建业务Key
        String businessKey = request.getFormType() + ":" + request.getFormId();

        // 发起流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                request.getProcessDefinitionKey(),
                businessKey,
                variables
        );

        // 保存表单-流程关联
        WfFormInstance formInstance = new WfFormInstance();
        formInstance.setFormType(request.getFormType());
        formInstance.setFormId(request.getFormId());
        formInstance.setProcessDefinitionKey(request.getProcessDefinitionKey());
        formInstance.setProcessInstanceId(processInstance.getId());
        formInstance.setStatus(1); // 审批中
        formInstance.setInitiatorId(user.getId());
        formInstance.setInitiatorName(user.getUsername());
        formInstance.setTitle(request.getTitle());
        formInstance.setCreatedAt(LocalDateTime.now());
        formInstance.setUpdatedAt(LocalDateTime.now());
        formInstanceMapper.insert(formInstance);

        return convertToVO(processInstance);
    }

    @Override
    @Transactional
    public void cancel(String processInstanceId, String reason) {
        CustomUserDetails user = getCurrentUser();

        // 检查流程是否存在且是当前用户发起的
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (hpi == null) {
            throw new BusinessException("流程实例不存在");
        }

        if (!user.getId().toString().equals(hpi.getStartUserId())) {
            throw new BusinessException("只能撤销自己发起的流程");
        }

        if (hpi.getEndTime() != null) {
            throw new BusinessException("流程已结束，无法撤销");
        }

        // 删除流程实例
        runtimeService.deleteProcessInstance(processInstanceId, "用户撤销: " + reason);

        // 更新表单-流程关联状态
        LambdaQueryWrapper<WfFormInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfFormInstance::getProcessInstanceId, processInstanceId);
        WfFormInstance formInstance = formInstanceMapper.selectOne(wrapper);
        if (formInstance != null) {
            formInstance.setStatus(3); // 撤销
            formInstance.setUpdatedAt(LocalDateTime.now());
            formInstanceMapper.updateById(formInstance);
        }
    }

    @Override
    public List<ProcessInstanceVO> listMyProcesses(int pageNum, int pageSize) {
        CustomUserDetails user = getCurrentUser();

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .startedBy(user.getId().toString())
                .orderByProcessInstanceStartTime().desc();

        return query.listPage((pageNum - 1) * pageSize, pageSize).stream()
                .map(this::convertHistoricToVO)
                .collect(Collectors.toList());
    }

    @Override
    public ProcessInstanceVO getById(String processInstanceId) {
        // 先查运行中的
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (pi != null) {
            return convertToVO(pi);
        }

        // 查历史的
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (hpi == null) {
            throw new BusinessException("流程实例不存在");
        }

        return convertHistoricToVO(hpi);
    }

    @Override
    public byte[] getProcessDiagram(String processInstanceId) {
        // 简化实现，返回null表示暂不支持
        return null;
    }

    private ProcessInstanceVO convertToVO(ProcessInstance pi) {
        ProcessInstanceVO vo = new ProcessInstanceVO();
        vo.setId(pi.getId());
        vo.setProcessDefinitionId(pi.getProcessDefinitionId());
        vo.setProcessDefinitionKey(pi.getProcessDefinitionKey());
        vo.setProcessDefinitionName(pi.getProcessDefinitionName());
        vo.setBusinessKey(pi.getBusinessKey());
        vo.setStartUserId(pi.getStartUserId());
        vo.setStartTime(pi.getStartTime());
        vo.setSuspended(pi.isSuspended());
        vo.setEnded(false);

        // 获取当前节点
        List<Execution> executions = runtimeService.createExecutionQuery()
                .processInstanceId(pi.getId())
                .onlyChildExecutions()
                .list();
        if (!executions.isEmpty()) {
            vo.setCurrentActivityName(executions.get(0).getActivityId());
        }

        return vo;
    }

    private ProcessInstanceVO convertHistoricToVO(HistoricProcessInstance hpi) {
        ProcessInstanceVO vo = new ProcessInstanceVO();
        vo.setId(hpi.getId());
        vo.setProcessDefinitionId(hpi.getProcessDefinitionId());
        vo.setProcessDefinitionKey(hpi.getProcessDefinitionKey());
        vo.setProcessDefinitionName(hpi.getProcessDefinitionName());
        vo.setBusinessKey(hpi.getBusinessKey());
        vo.setStartUserId(hpi.getStartUserId());
        vo.setStartTime(hpi.getStartTime());
        vo.setEndTime(hpi.getEndTime());
        vo.setEnded(hpi.getEndTime() != null);

        return vo;
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new BusinessException("请先登录");
        }
        return (CustomUserDetails) auth.getPrincipal();
    }
}
