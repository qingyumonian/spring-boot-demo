package com.lxf.demo.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报销申请实体
 */
@Data
@TableName("wf_expense")
public class WfExpense {

    @TableId(type = IdType.AUTO)
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
     * 附件路径（JSON数组）
     */
    private String attachments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
