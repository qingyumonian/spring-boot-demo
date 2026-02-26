package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

import java.util.Map;

/**
 * 任务完成请求
 */
@Data
public class TaskCompleteRequest {

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;
}
