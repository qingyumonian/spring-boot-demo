package com.lxf.demo.security.provider;

import com.lxf.demo.modules.entity.User;
import com.lxf.demo.modules.service.IUserService;
import com.lxf.demo.security.encoder.Md5PasswordEncoder;
import com.lxf.demo.security.userdetails.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * 自定义认证提供者 - 使用MD5验证密码
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private IUserService userService;

    @Resource
    private Md5PasswordEncoder md5PasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 通过userService查询用户
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BadCredentialsException("用户已被禁用");
        }

        // 使用MD5验证密码
        if (!md5PasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        // 构建认证对象
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
