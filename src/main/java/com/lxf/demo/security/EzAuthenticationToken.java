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
    private String accessToekn;

    @Getter
    @Setter
    private CustomUserDetails userDetails;

    public EzAuthenticationToken(CustomUserDetails userDetails, String accessToekn) {
        super(userDetails.getAuthorities());
        this.userDetails = userDetails;
        this.accessToekn = accessToekn;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
