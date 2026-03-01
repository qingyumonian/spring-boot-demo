package com.lxf.demo.dubbo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.api.dto.PageResult;
import com.lxf.demo.api.dto.RoleDTO;
import com.lxf.demo.api.service.DubboRoleService;
import com.lxf.demo.modules.entity.SysRole;
import com.lxf.demo.modules.service.IRoleService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class DubboRoleServiceImpl implements DubboRoleService {

    @Autowired
    private IRoleService roleService;

    @Override
    public RoleDTO getRoleById(Long id) {
        SysRole role = roleService.getRoleById(id);
        return convertToDTO(role);
    }

    @Override
    public RoleDTO findByRoleKey(String roleKey) {
        SysRole role = roleService.findByRoleKey(roleKey);
        return convertToDTO(role);
    }

    @Override
    public PageResult<RoleDTO> getRolePage(int pageNum, int pageSize) {
        IPage<SysRole> page = roleService.getRolePage(pageNum, pageSize);
        List<RoleDTO> records = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(records, page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleService.getAllRoles().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleDTO> getRolesByUserId(Long userId) {
        return roleService.getRolesByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RoleDTO convertToDTO(SysRole role) {
        if (role == null) {
            return null;
        }
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(role, dto);
        return dto;
    }
}
