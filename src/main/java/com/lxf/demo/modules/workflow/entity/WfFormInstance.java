package com.lxf.demo.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表单-流程关联实体
 */
@Data
@TableName("wf_form_instance")
public class WfFormInstance {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表单类型
     */
    private String formType;

    /**
     * 业务表单ID
     */
    private Long formId;

    /**
     * 流程定义KEY
     */
    private String processDefinitionKey;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 状态：0草稿/1审批中/2完成/3撤销/4拒绝
     */
    private Integer status;

    /**
     * 发起人ID
     */
    private Long initiatorId;

    /**
     * 发起人名称
     */
    private String initiatorName;

    /**
     * 流程标题
     */
    private String title;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
