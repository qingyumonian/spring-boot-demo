package com.lxf.demo.modules.workflow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 报销申请请求
 */
@Data
public class ExpenseApplyRequest {

    /**
     * 报销类型
     */
    @NotBlank(message = "报销类型不能为空")
    private String expenseType;

    /**
     * 报销金额
     */
    @NotNull(message = "报销金额不能为空")
    private BigDecimal amount;

    /**
     * 报销事由
     */
    private String reason;

    /**
     * 附件路径（JSON数组）
     */
    private String attachments;
}
