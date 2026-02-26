package com.lxf.demo.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxf.demo.common.exception.BusinessException;
import com.lxf.demo.modules.workflow.dto.ReturnableActivityVO;
import com.lxf.demo.modules.workflow.dto.TaskCompleteRequest;
import com.lxf.demo.modules.workflow.dto.TaskRejectRequest;
import com.lxf.demo.modules.workflow.dto.TaskVO;
import com.lxf.demo.modules.workflow.entity.WfFormInstance;
import com.lxf.demo.modules.workflow.mapper.WfFormInstanceMapper;
import com.lxf.demo.modules.workflow.service.ITaskService;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 任务服务实现
 */
@Service
public class TaskServiceImpl implements ITaskService {

    @Resource
    private TaskService taskService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private WfFormInstanceMapper formInstanceMapper;

    @Override
    public List<TaskVO> listTodoTasks(int pageNum, int pageSize) {
        CustomUserDetails user = getCurrentUser();

        TaskQuery query = taskService.createTaskQuery()
                .taskCandidateOrAssigned(user.getId().toString())
                .orderByTaskCreateTime().desc();

        return query.listPage((pageNum - 1) * pageSize, pageSize).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskVO> listDoneTasks(int pageNum, int pageSize) {
        CustomUserDetails user = getCurrentUser();

        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(user.getId().toString())
                .finished()
                .orderByHistoricTaskInstanceEndTime().desc();

        return query.listPage((pageNum - 1) * pageSize, pageSize).stream()
                .map(this::convertHistoricToVO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskVO getById(String taskId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        return convertToVO(task);
    }

    @Override
    @Transactional
    public void complete(String taskId, TaskCompleteRequest request) {
        CustomUserDetails user = getCurrentUser();

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 如果任务未签收，先签收
        if (task.getAssignee() == null) {
            taskService.claim(taskId, user.getId().toString());
        }

        // 添加审批意见
        if (StringUtils.hasText(request.getComment())) {
            taskService.addComment(taskId, task.getProcessInstanceId(), request.getComment());
        }

        // 完成任务
        if (request.getVariables() != null) {
            taskService.complete(taskId, request.getVariables());
        } else {
            taskService.complete(taskId);
        }

        // 检查流程是否结束，更新表单状态
        checkAndUpdateFormStatus(task.getProcessInstanceId());
    }

    @Override
    @Transactional
    public void reject(String taskId, TaskRejectRequest request) {
        CustomUserDetails user = getCurrentUser();

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 添加退回原因
        if (StringUtils.hasText(request.getReason())) {
            taskService.addComment(taskId, task.getProcessInstanceId(), "退回: " + request.getReason());
        }

        // 使用changeActivityState实现退回任意节点
        String currentActivityId = task.getTaskDefinitionKey();
        String targetActivityId = request.getTargetActivityId();

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(currentActivityId, targetActivityId)
                .changeState();
    }

    @Override
    public List<ReturnableActivityVO> getReturnableActivities(String taskId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        String processInstanceId = task.getProcessInstanceId();
        String processDefinitionId = task.getProcessDefinitionId();

        // 获取已执行过的用户任务节点
        List<HistoricActivityInstance> historicActivities = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();

        // 获取BPMN模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        Set<String> activityIds = new HashSet<>();
        List<ReturnableActivityVO> result = new ArrayList<>();

        for (HistoricActivityInstance activity : historicActivities) {
            // 避免重复
            if (activityIds.contains(activity.getActivityId())) {
                continue;
            }
            // 排除当前节点
            if (activity.getActivityId().equals(task.getTaskDefinitionKey())) {
                continue;
            }

            activityIds.add(activity.getActivityId());

            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess()
                    .getFlowElement(activity.getActivityId());

            ReturnableActivityVO vo = new ReturnableActivityVO();
            vo.setActivityId(activity.getActivityId());
            vo.setActivityName(activity.getActivityName());
            vo.setActivityType(activity.getActivityType());
            result.add(vo);
        }

        return result;
    }

    @Override
    public void claim(String taskId) {
        CustomUserDetails user = getCurrentUser();
        taskService.claim(taskId, user.getId().toString());
    }

    @Override
    public void unclaim(String taskId) {
        taskService.unclaim(taskId);
    }

    @Override
    public void transfer(String taskId, String targetUserId) {
        taskService.setAssignee(taskId, targetUserId);
    }

    private TaskVO convertToVO(Task task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setName(task.getName());
        vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        vo.setAssignee(task.getAssignee());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        vo.setCreateTime(task.getCreateTime());

        // 获取流程定义名称
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();
        if (pd != null) {
            vo.setProcessDefinitionName(pd.getName());
        }

        // 获取表单关联信息
        LambdaQueryWrapper<WfFormInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfFormInstance::getProcessInstanceId, task.getProcessInstanceId());
        WfFormInstance formInstance = formInstanceMapper.selectOne(wrapper);
        if (formInstance != null) {
            vo.setFormType(formInstance.getFormType());
            vo.setFormId(formInstance.getFormId());
            vo.setTitle(formInstance.getTitle());
            vo.setInitiatorName(formInstance.getInitiatorName());
        }

        return vo;
    }

    private TaskVO convertHistoricToVO(HistoricTaskInstance task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setName(task.getName());
        vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        vo.setAssignee(task.getAssignee());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        vo.setCreateTime(task.getCreateTime());

        // 获取流程定义名称
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();
        if (pd != null) {
            vo.setProcessDefinitionName(pd.getName());
        }

        return vo;
    }

    private void checkAndUpdateFormStatus(String processInstanceId) {
        // 检查流程是否结束
        long count = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .count();

        if (count == 0) {
            // 流程已结束，更新表单状态
            LambdaQueryWrapper<WfFormInstance> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WfFormInstance::getProcessInstanceId, processInstanceId);
            WfFormInstance formInstance = formInstanceMapper.selectOne(wrapper);
            if (formInstance != null) {
                formInstance.setStatus(2); // 完成
                formInstance.setUpdatedAt(LocalDateTime.now());
                formInstanceMapper.updateById(formInstance);
            }
        }
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new BusinessException("请先登录");
        }
        return (CustomUserDetails) auth.getPrincipal();
    }
}
