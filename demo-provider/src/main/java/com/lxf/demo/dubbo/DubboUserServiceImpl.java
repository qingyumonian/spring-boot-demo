package com.lxf.demo.dubbo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.api.dto.PageResult;
import com.lxf.demo.api.dto.UserDTO;
import com.lxf.demo.api.service.DubboUserService;
import com.lxf.demo.modules.entity.SysUser;
import com.lxf.demo.modules.service.IUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@DubboService
public class DubboUserServiceImpl implements DubboUserService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDTO getUserById(Long id) {
        SysUser user = userService.getUserById(id);
        return convertToDTO(user);
    }

    @Override
    public UserDTO findByUsername(String username) {
        SysUser user = userService.findByUsername(username);
        return convertToDTO(user);
    }

    @Override
    public PageResult<UserDTO> getUserPage(int pageNum, int pageSize) {
        IPage<SysUser> page = userService.getUserPage(pageNum, pageSize);
        List<UserDTO> records = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(records, page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getPermissionsByUserId(Long userId) {
        return userService.getPermissionsByUserId(userId);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userService.getUserRoleIds(userId);
    }

    private UserDTO convertToDTO(SysUser user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
