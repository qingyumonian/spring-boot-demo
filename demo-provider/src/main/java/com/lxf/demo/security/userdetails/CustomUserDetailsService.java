package com.lxf.demo.security.userdetails;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lxf.demo.modules.entity.SysUser;
import com.lxf.demo.modules.service.IUserService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("SysUser not found: " + username);
        }
        Set<String> permissions = userService.getPermissionsByUserId(user.getId());
        String[] permArray = permissions.stream().filter(StringUtils::isNotBlank).toArray(String[]::new);
        return new CustomUserDetails(user.getId(),user.getUsername(),user.getPassword(), AuthorityUtils.createAuthorityList(permArray) );
    }
}
