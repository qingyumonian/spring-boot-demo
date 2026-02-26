package com.lxf.demo.modules.workflow.service.impl;

import com.lxf.demo.common.exception.BusinessException;
import com.lxf.demo.modules.workflow.dto.ProcessDefinitionVO;
import com.lxf.demo.modules.workflow.dto.ProcessDeployRequest;
import com.lxf.demo.modules.workflow.service.IProcessDefinitionService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程定义服务实现
 */
@Service
public class ProcessDefinitionServiceImpl implements IProcessDefinitionService {

    @Resource
    private RepositoryService repositoryService;

    @Override
    public ProcessDefinitionVO deploy(ProcessDeployRequest request) {
        try {
            InputStream bpmnStream = new ByteArrayInputStream(
                    request.getBpmnXml().getBytes(StandardCharsets.UTF_8));

            Deployment deployment = repositoryService.createDeployment()
                    .name(request.getName())
                    .category(request.getCategory())
                    .addInputStream(request.getName() + ".bpmn20.xml", bpmnStream)
                    .deploy();

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .singleResult();

            return convertToVO(processDefinition, deployment);
        } catch (Exception e) {
            throw new BusinessException("流程部署失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(String deploymentId, boolean cascade) {
        try {
            repositoryService.deleteDeployment(deploymentId, cascade);
        } catch (Exception e) {
            throw new BusinessException("删除流程定义失败: " + e.getMessage());
        }
    }

    @Override
    public List<ProcessDefinitionVO> list(String name, String category) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionName().asc();

        if (StringUtils.hasText(name)) {
            query.processDefinitionNameLike("%" + name + "%");
        }
        if (StringUtils.hasText(category)) {
            query.processDefinitionCategory(category);
        }

        return query.list().stream()
                .map(pd -> {
                    Deployment deployment = repositoryService.createDeploymentQuery()
                            .deploymentId(pd.getDeploymentId())
                            .singleResult();
                    return convertToVO(pd, deployment);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProcessDefinitionVO getById(String processDefinitionId) {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        if (pd == null) {
            throw new BusinessException("流程定义不存在");
        }

        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(pd.getDeploymentId())
                .singleResult();

        return convertToVO(pd, deployment);
    }

    @Override
    public String getBpmnXml(String processDefinitionId) {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        if (pd == null) {
            throw new BusinessException("流程定义不存在");
        }

        try (InputStream inputStream = repositoryService.getResourceAsStream(
                pd.getDeploymentId(), pd.getResourceName())) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BusinessException("获取BPMN内容失败: " + e.getMessage());
        }
    }

    @Override
    public void suspend(String processDefinitionId) {
        repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
    }

    @Override
    public void activate(String processDefinitionId) {
        repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
    }

    private ProcessDefinitionVO convertToVO(ProcessDefinition pd, Deployment deployment) {
        ProcessDefinitionVO vo = new ProcessDefinitionVO();
        vo.setId(pd.getId());
        vo.setKey(pd.getKey());
        vo.setName(pd.getName());
        vo.setVersion(pd.getVersion());
        vo.setCategory(pd.getCategory());
        vo.setDeploymentId(pd.getDeploymentId());
        vo.setResourceName(pd.getResourceName());
        vo.setSuspended(pd.isSuspended());
        if (deployment != null) {
            vo.setDeploymentTime(deployment.getDeploymentTime());
        }
        return vo;
    }
}
