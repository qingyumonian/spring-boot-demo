package com.lxf.demo.modules.workflow.service;

import com.lxf.demo.modules.workflow.dto.ReturnableActivityVO;
import com.lxf.demo.modules.workflow.dto.TaskCompleteRequest;
import com.lxf.demo.modules.workflow.dto.TaskRejectRequest;
import com.lxf.demo.modules.workflow.dto.TaskVO;

import java.util.List;

/**
 * 任务服务
 */
public interface ITaskService {

    /**
     * 查询待办任务
     */
    List<TaskVO> listTodoTasks(int pageNum, int pageSize);

    /**
     * 查询已办任务
     */
    List<TaskVO> listDoneTasks(int pageNum, int pageSize);

    /**
     * 获取任务详情
     */
    TaskVO getById(String taskId);

    /**
     * 完成任务
     */
    void complete(String taskId, TaskCompleteRequest request);

    /**
     * 退回任务
     */
    void reject(String taskId, TaskRejectRequest request);

    /**
     * 获取可退回的活动节点
     */
    List<ReturnableActivityVO> getReturnableActivities(String taskId);

    /**
     * 签收任务
     */
    void claim(String taskId);

    /**
     * 取消签收
     */
    void unclaim(String taskId);

    /**
     * 转办任务
     */
    void transfer(String taskId, String targetUserId);
}
