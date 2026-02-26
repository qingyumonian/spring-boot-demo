package com.lxf.demo.modules.workflow.controller;

import com.lxf.demo.common.result.R;
import com.lxf.demo.modules.workflow.dto.ProcessInstanceVO;
import com.lxf.demo.modules.workflow.dto.ProcessStartRequest;
import com.lxf.demo.modules.workflow.service.IProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 流程实例控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/workflow/instances")
public class ProcessInstanceController {

    @Resource
    private IProcessInstanceService processInstanceService;

    /**
     * 发起流程
     */
    @PostMapping("/start")
    @PreAuthorize("hasAuthority('workflow:instance:start')")
    public R<ProcessInstanceVO> start(@Valid @RequestBody ProcessStartRequest request) {
        ProcessInstanceVO result = processInstanceService.start(request);
        return R.ok(result);
    }

    /**
     * 撤销流程
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('workflow:instance:cancel')")
    public R<Void> cancel(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        processInstanceService.cancel(id, reason);
        return R.ok();
    }

    /**
     * 查询我的流程
     */
    @GetMapping("/my")
    public R<List<ProcessInstanceVO>> listMyProcesses(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<ProcessInstanceVO> result = processInstanceService.listMyProcesses(pageNum, pageSize);
        return R.ok(result);
    }

    /**
     * 获取流程实例详情
     */
    @GetMapping("/{id}")
    public R<ProcessInstanceVO> getById(@PathVariable String id) {
        ProcessInstanceVO result = processInstanceService.getById(id);
        return R.ok(result);
    }
}
