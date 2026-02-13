package com.lxf.demo.security.sso.cas;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * CAS用户信息
 * 封装CAS票据验证后返回的用户信息
 */
@Data
public class CasUserInfo {

    /**
     * 用户名（CAS主体）
     */
    private String username;

    /**
     * 用户属性（CAS返回的额外属性）
     */
    private Map<String, String> attributes = new HashMap<>();

    /**
     * 获取邮箱属性
     */
    public String getEmail() {
        // CAS可能使用不同的属性名
        String email = attributes.get("mail");
        if (email == null) {
            email = attributes.get("email");
        }
        return email;
    }

    /**
     * 添加属性
     */
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }
}
