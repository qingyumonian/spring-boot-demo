package com.lxf.demo.modules.service;

import com.lxf.demo.security.userdetails.CustomUserDetails;

public interface ITokenService {

    String saveAccessToken(CustomUserDetails userDetails);

    CustomUserDetails getUserByToken(String token);

    void removeAccessToken(String token);

    boolean validateToken(String token);
}