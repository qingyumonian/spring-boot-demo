package com.lxf.demo.security;

import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 认证信息-》包含用户信息，权限信息，token 在SecurityContext中流转
 * @author lixuefei
 * @date 2026/02/10 20:31
 **/

public class AuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    @Setter
    private CustomUserDetails userDetails;

    @Getter
    private final String accessToken;



    public AuthenticationToken(CustomUserDetails userDetails, String accessToken) {
        super(userDetails.getAuthorities());
        this.userDetails = userDetails;
        this.accessToken = accessToken;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public Object getPrincipal() {


        return userDetails;
    }
}
