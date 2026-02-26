package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

/**
 * 可退回的活动节点VO
 */
@Data
public class ReturnableActivityVO {

    /**
     * 活动ID
     */
    private String activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动类型
     */
    private String activityType;
}
