package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 流程发起请求
 */
@Data
public class ProcessStartRequest {

    /**
     * 流程定义Key
     */
    @NotBlank(message = "流程定义Key不能为空")
    private String processDefinitionKey;

    /**
     * 业务表单类型
     */
    @NotBlank(message = "表单类型不能为空")
    private String formType;

    /**
     * 业务表单ID
     */
    @NotNull(message = "表单ID不能为空")
    private Long formId;

    /**
     * 流程标题
     */
    private String title;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;
}
