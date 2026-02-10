package com.lxf.demo.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenProvider {

    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
