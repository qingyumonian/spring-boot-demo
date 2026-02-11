package com.lxf.demo.security;

import com.lxf.demo.security.userdetails.CustomUserDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author lixuefei
 * @date 2026/02/10 20:31
 **/

public class EzAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    @Setter
    private CustomUserDetails userDetails;

    @Getter
    private final String accessToken;



    public EzAuthenticationToken(CustomUserDetails userDetails, String accessToken) {
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
