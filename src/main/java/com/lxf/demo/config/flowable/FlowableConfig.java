package com.lxf.demo.config.flowable;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable流程引擎配置
 */
@Configuration
public class FlowableConfig {

    /**
     * 配置流程引擎
     * - 设置中文字体支持
     * - 禁用自动部署（手动部署流程）
     */
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> engineConfigurationConfigurer() {
        return engineConfiguration -> {
            // 设置中文字体，避免流程图中文乱码
            engineConfiguration.setActivityFontName("宋体");
            engineConfiguration.setLabelFontName("宋体");
            engineConfiguration.setAnnotationFontName("宋体");
            // 禁用自动部署processes目录下的流程定义
//            engineConfiguration.setDisableBpmnHttpConnector(true);
        };
    }
}
