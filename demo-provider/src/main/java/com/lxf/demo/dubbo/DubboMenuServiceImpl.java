package com.lxf.demo.dubbo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxf.demo.api.dto.MenuDTO;
import com.lxf.demo.api.dto.PageResult;
import com.lxf.demo.api.service.DubboMenuService;
import com.lxf.demo.modules.entity.SysMenu;
import com.lxf.demo.modules.service.IMenuService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class DubboMenuServiceImpl implements DubboMenuService {

    @Autowired
    private IMenuService menuService;

    @Override
    public MenuDTO getMenuById(Long id) {
        SysMenu menu = menuService.getMenuById(id);
        return convertToDTO(menu);
    }

    @Override
    public PageResult<MenuDTO> getMenuPage(int pageNum, int pageSize) {
        IPage<SysMenu> page = menuService.getMenuPage(pageNum, pageSize);
        List<MenuDTO> records = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(records, page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public List<MenuDTO> getAllMenus() {
        return menuService.getAllMenus().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getMenuTree() {
        return menuService.getMenuTree().stream()
                .map(this::convertToDTOWithChildren)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getMenusByRoleId(Long roleId) {
        return menuService.getMenusByRoleId(roleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getMenuTreeByRoleIds(List<Long> roleIds) {
        return menuService.getMenuTreeByRoleIds(roleIds).stream()
                .map(this::convertToDTOWithChildren)
                .collect(Collectors.toList());
    }

    private MenuDTO convertToDTO(SysMenu menu) {
        if (menu == null) {
            return null;
        }
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(menu, dto, "children");
        return dto;
    }

    private MenuDTO convertToDTOWithChildren(SysMenu menu) {
        if (menu == null) {
            return null;
        }
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(menu, dto, "children");
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            dto.setChildren(menu.getChildren().stream()
                    .map(this::convertToDTOWithChildren)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
