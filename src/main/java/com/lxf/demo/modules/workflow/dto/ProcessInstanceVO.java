package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 流程实例VO
 */
@Data
public class ProcessInstanceVO {

    /**
     * 流程实例ID
     */
    private String id;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 流程定义名称
     */
    private String processDefinitionName;

    /**
     * 业务Key
     */
    private String businessKey;

    /**
     * 发起人ID
     */
    private String startUserId;

    /**
     * 发起人名称
     */
    private String startUserName;

    /**
     * 发起时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 当前节点名称
     */
    private String currentActivityName;

    /**
     * 是否挂起
     */
    private Boolean suspended;

    /**
     * 是否结束
     */
    private Boolean ended;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;
}
