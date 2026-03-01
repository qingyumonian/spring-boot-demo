package com.lxf.demo.modules.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SSO登录URL响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoLoginUrlResponse {

    /**
     * SSO提供商名称
     */
    private String provider;

    /**
     * SSO登录URL
     */
    private String loginUrl;

    /**
     * 状态参数（用于防CSRF）
     */
    private String state;
}
