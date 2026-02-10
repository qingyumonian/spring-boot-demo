package com.lxf.demo.modules.auth.service;

import com.lxf.demo.security.userdetails.CustomUserDetails;

public interface TokenService {

    String saveAccessToken(CustomUserDetails userDetails);

    CustomUserDetails getUserByToken(String token);

    void removeAccessToken(String token);

    boolean validateToken(String token);
}
