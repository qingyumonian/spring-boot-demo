package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报销VO
 */
@Data
public class ExpenseVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 报销单号
     */
    private String expenseNo;

    /**
     * 报销类型
     */
    private String expenseType;

    /**
     * 报销金额
     */
    private BigDecimal amount;

    /**
     * 报销事由
     */
    private String reason;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 状态：0草稿/1审批中/2已通过/3已拒绝/4已撤销
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 附件路径
     */
    private String attachments;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 当前节点名称
     */
    private String currentActivityName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
