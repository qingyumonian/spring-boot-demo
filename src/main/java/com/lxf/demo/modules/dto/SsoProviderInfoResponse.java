package com.lxf.demo.modules.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * SSO提供商信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoProviderInfoResponse {

    /**
     * 提供商名称
     */
    private String name;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 额外信息
     */
    private Map<String, Object> info;
}
