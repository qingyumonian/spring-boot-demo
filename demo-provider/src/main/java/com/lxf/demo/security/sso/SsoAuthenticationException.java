package com.lxf.demo.security.sso;

/**
 * SSO认证异常
 * 封装SSO认证过程中的各种错误
 */
public class SsoAuthenticationException extends RuntimeException {

    /**
     * SSO提供商名称
     */
    private final String provider;

    /**
     * 错误代码
     */
    private final String errorCode;

    public SsoAuthenticationException(String provider, String errorCode, String message) {
        super(message);
        this.provider = provider;
        this.errorCode = errorCode;
    }

    public SsoAuthenticationException(String provider, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.provider = provider;
        this.errorCode = errorCode;
    }

    public String getProvider() {
        return provider;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
