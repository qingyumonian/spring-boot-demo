package com.lxf.demo.modules.workflow.service;

import com.lxf.demo.modules.workflow.dto.ProcessInstanceVO;
import com.lxf.demo.modules.workflow.dto.ProcessStartRequest;

import java.util.List;

/**
 * 流程实例服务
 */
public interface IProcessInstanceService {

    /**
     * 发起流程
     */
    ProcessInstanceVO start(ProcessStartRequest request);

    /**
     * 撤销流程
     */
    void cancel(String processInstanceId, String reason);

    /**
     * 查询我的流程
     */
    List<ProcessInstanceVO> listMyProcesses(int pageNum, int pageSize);

    /**
     * 获取流程实例详情
     */
    ProcessInstanceVO getById(String processInstanceId);

    /**
     * 获取流程图
     */
    byte[] getProcessDiagram(String processInstanceId);
}
