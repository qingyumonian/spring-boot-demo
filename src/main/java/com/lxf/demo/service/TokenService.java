package com.lxf.demo.service;

import com.lxf.demo.security.CustomUserDetails;

public interface TokenService {

    String saveAccessToken(CustomUserDetails userDetails);

    CustomUserDetails getUserByToken(String token);

    void removeAccessToken(String token);

    boolean validateToken(String token);
}
