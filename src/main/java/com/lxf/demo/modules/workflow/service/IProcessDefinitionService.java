package com.lxf.demo.modules.workflow.service;

import com.lxf.demo.modules.workflow.dto.ProcessDefinitionVO;
import com.lxf.demo.modules.workflow.dto.ProcessDeployRequest;

import java.util.List;

/**
 * 流程定义服务
 */
public interface IProcessDefinitionService {

    /**
     * 部署流程
     */
    ProcessDefinitionVO deploy(ProcessDeployRequest request);

    /**
     * 删除流程定义
     */
    void delete(String deploymentId, boolean cascade);

    /**
     * 查询流程定义列表
     */
    List<ProcessDefinitionVO> list(String name, String category);

    /**
     * 获取流程定义详情
     */
    ProcessDefinitionVO getById(String processDefinitionId);

    /**
     * 获取流程定义BPMN XML
     */
    String getBpmnXml(String processDefinitionId);

    /**
     * 挂起流程定义
     */
    void suspend(String processDefinitionId);

    /**
     * 激活流程定义
     */
    void activate(String processDefinitionId);
}
