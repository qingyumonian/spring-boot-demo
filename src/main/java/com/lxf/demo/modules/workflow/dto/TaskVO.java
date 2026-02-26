package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 任务VO
 */
@Data
public class TaskVO {

    /**
     * 任务ID
     */
    private String id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务定义Key
     */
    private String taskDefinitionKey;

    /**
     * 处理人ID
     */
    private String assignee;

    /**
     * 处理人名称
     */
    private String assigneeName;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 流程定义名称
     */
    private String processDefinitionName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 表单类型
     */
    private String formType;

    /**
     * 表单ID
     */
    private Long formId;

    /**
     * 流程标题
     */
    private String title;

    /**
     * 发起人名称
     */
    private String initiatorName;

    /**
     * 任务变量
     */
    private Map<String, Object> variables;
}
