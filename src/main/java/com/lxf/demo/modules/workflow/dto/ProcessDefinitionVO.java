package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

import java.util.Date;

/**
 * 流程定义VO
 */
@Data
public class ProcessDefinitionVO {

    /**
     * 流程定义ID
     */
    private String id;

    /**
     * 流程定义Key
     */
    private String key;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 流程分类
     */
    private String category;

    /**
     * 部署ID
     */
    private String deploymentId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 是否挂起
     */
    private Boolean suspended;

    /**
     * 部署时间
     */
    private Date deploymentTime;
}
