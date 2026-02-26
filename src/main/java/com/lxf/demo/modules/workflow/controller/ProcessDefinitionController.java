package com.lxf.demo.modules.workflow.controller;

import com.lxf.demo.common.result.R;
import com.lxf.demo.modules.workflow.dto.ProcessDefinitionVO;
import com.lxf.demo.modules.workflow.dto.ProcessDeployRequest;
import com.lxf.demo.modules.workflow.service.IProcessDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 流程定义控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/workflow/definitions")
public class ProcessDefinitionController {

    @Resource
    private IProcessDefinitionService processDefinitionService;

    /**
     * 部署流程定义
     */
    @PostMapping("/deploy")
    @PreAuthorize("hasAuthority('workflow:definition:deploy')")
    public R<ProcessDefinitionVO> deploy(@Valid @RequestBody ProcessDeployRequest request) {
        ProcessDefinitionVO result = processDefinitionService.deploy(request);
        return R.ok(result);
    }

    /**
     * 删除流程定义
     */
    @DeleteMapping("/{deploymentId}")
    @PreAuthorize("hasAuthority('workflow:definition:delete')")
    public R<Void> delete(
            @PathVariable String deploymentId,
            @RequestParam(defaultValue = "false") boolean cascade) {
        processDefinitionService.delete(deploymentId, cascade);
        return R.ok();
    }

    /**
     * 查询流程定义列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('workflow:definition:list')")
    public R<List<ProcessDefinitionVO>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        List<ProcessDefinitionVO> result = processDefinitionService.list(name, category);
        return R.ok(result);
    }

    /**
     * 获取流程定义详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:definition:query')")
    public R<ProcessDefinitionVO> getById(@PathVariable String id) {
        ProcessDefinitionVO result = processDefinitionService.getById(id);
        return R.ok(result);
    }

    /**
     * 获取流程定义BPMN XML
     */
    @GetMapping("/{id}/bpmn")
    @PreAuthorize("hasAuthority('workflow:definition:query')")
    public R<String> getBpmnXml(@PathVariable String id) {
        String bpmnXml = processDefinitionService.getBpmnXml(id);
        return R.ok(bpmnXml);
    }

    /**
     * 挂起流程定义
     */
    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasAuthority('workflow:definition:edit')")
    public R<Void> suspend(@PathVariable String id) {
        processDefinitionService.suspend(id);
        return R.ok();
    }

    /**
     * 激活流程定义
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('workflow:definition:edit')")
    public R<Void> activate(@PathVariable String id) {
        processDefinitionService.activate(id);
        return R.ok();
    }
}
