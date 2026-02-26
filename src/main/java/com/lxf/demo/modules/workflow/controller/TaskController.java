package com.lxf.demo.modules.workflow.controller;

import com.lxf.demo.common.result.R;
import com.lxf.demo.modules.workflow.dto.ReturnableActivityVO;
import com.lxf.demo.modules.workflow.dto.TaskCompleteRequest;
import com.lxf.demo.modules.workflow.dto.TaskRejectRequest;
import com.lxf.demo.modules.workflow.dto.TaskVO;
import com.lxf.demo.modules.workflow.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/workflow/tasks")
public class TaskController {

    @Resource
    private ITaskService taskService;

    /**
     * 查询待办任务
     */
    @GetMapping("/todo")
    public R<List<TaskVO>> listTodoTasks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<TaskVO> result = taskService.listTodoTasks(pageNum, pageSize);
        return R.ok(result);
    }

    /**
     * 查询已办任务
     */
    @GetMapping("/done")
    public R<List<TaskVO>> listDoneTasks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<TaskVO> result = taskService.listDoneTasks(pageNum, pageSize);
        return R.ok(result);
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    public R<TaskVO> getById(@PathVariable String id) {
        TaskVO result = taskService.getById(id);
        return R.ok(result);
    }

    /**
     * 完成任务
     */
    @PostMapping("/{id}/complete")
    public R<Void> complete(
            @PathVariable String id,
            @RequestBody TaskCompleteRequest request) {
        taskService.complete(id, request);
        return R.ok();
    }

    /**
     * 退回任务
     */
    @PostMapping("/{id}/reject")
    public R<Void> reject(
            @PathVariable String id,
            @Valid @RequestBody TaskRejectRequest request) {
        taskService.reject(id, request);
        return R.ok();
    }

    /**
     * 获取可退回的节点列表
     */
    @GetMapping("/{id}/returnable-activities")
    public R<List<ReturnableActivityVO>> getReturnableActivities(@PathVariable String id) {
        List<ReturnableActivityVO> result = taskService.getReturnableActivities(id);
        return R.ok(result);
    }

    /**
     * 签收任务
     */
    @PostMapping("/{id}/claim")
    public R<Void> claim(@PathVariable String id) {
        taskService.claim(id);
        return R.ok();
    }

    /**
     * 取消签收
     */
    @PostMapping("/{id}/unclaim")
    public R<Void> unclaim(@PathVariable String id) {
        taskService.unclaim(id);
        return R.ok();
    }

    /**
     * 转办任务
     */
    @PostMapping("/{id}/transfer")
    public R<Void> transfer(
            @PathVariable String id,
            @RequestParam String targetUserId) {
        taskService.transfer(id, targetUserId);
        return R.ok();
    }
}
