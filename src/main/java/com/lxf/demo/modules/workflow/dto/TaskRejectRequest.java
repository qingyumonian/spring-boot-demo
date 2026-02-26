package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 任务退回请求
 */
@Data
public class TaskRejectRequest {

    /**
     * 目标活动ID（退回到的节点）
     */
    @NotBlank(message = "目标节点不能为空")
    private String targetActivityId;

    /**
     * 退回原因
     */
    private String reason;
}
