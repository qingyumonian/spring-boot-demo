package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 流程部署请求
 */
@Data
public class ProcessDeployRequest {

    /**
     * 流程名称
     */
    @NotBlank(message = "流程名称不能为空")
    private String name;

    /**
     * 流程分类
     */
    private String category;

    /**
     * BPMN XML内容
     */
    @NotBlank(message = "BPMN内容不能为空")
    private String bpmnXml;
}
