package com.lxf.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenUtil {

    public static String getSub(String token) {
        DecodedJWT jwt = JWT.decode(token);
        String sub = jwt.getClaim("sub").asString();
        return sub;
    }

}