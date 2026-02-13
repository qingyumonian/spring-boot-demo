package com.lxf.demo.config.sso.cas;

import lombok.Data;

/**
 * CAS 2.0 配置属性
 */
@Data
public class CasProperties {

    /**
     * 是否启用CAS认证
     */
    private boolean enabled = false;

    /**
     * CAS服务器URL前缀
     * 例如: https://cas.example.com/cas
     */
    private String serverUrlPrefix;

    /**
     * CAS服务器登录URL
     * 默认: {serverUrlPrefix}/login
     */
    private String serverLoginUrl;

    /**
     * CAS票据验证URL (CAS 2.0使用serviceValidate)
     * 默认: {serverUrlPrefix}/serviceValidate
     */
    private String serverValidateUrl;

    /**
     * 本应用地址
     * 例如: http://localhost:8888
     */
    private String clientHostUrl;

    /**
     * CAS回调路径
     * 默认: /api/auth/cas
     */
    private String loginPath = "/api/auth/cas";

    /**
     * 新用户默认角色
     */
    private String defaultRole = "USER";

    /**
     * 获取登录URL（如果未配置则自动生成）
     */
    public String getServerLoginUrl() {
        if (serverLoginUrl != null && !serverLoginUrl.isEmpty()) {
            return serverLoginUrl;
        }
        return serverUrlPrefix + "/login";
    }

    /**
     * 获取票据验证URL（如果未配置则自动生成）
     */
    public String getServerValidateUrl() {
        if (serverValidateUrl != null && !serverValidateUrl.isEmpty()) {
            return serverValidateUrl;
        }
        return serverUrlPrefix + "/serviceValidate";
    }

    /**
     * 获取完整的服务回调URL
     */
    public String getServiceUrl() {
        return clientHostUrl + loginPath;
    }
}
