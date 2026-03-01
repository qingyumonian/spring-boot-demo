package com.lxf.demo.security.sso;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SSO提供商注册表
 * 收集所有SsoProvider Bean，提供按名称查找功能
 */
@Slf4j
@Component
public class SsoProviderRegistry {

    private final Map<String, SsoProvider> providerMap;
    private final List<SsoProvider> providers;

    @Autowired
    public SsoProviderRegistry(List<SsoProvider> providers) {
        this.providers = providers != null ? providers : Collections.emptyList();
        this.providerMap = this.providers.stream()
                .collect(Collectors.toMap(
                        SsoProvider::getProviderName,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
    }

    @PostConstruct
    public void init() {
        log.info("SSO提供商注册表初始化完成，已注册 {} 个提供商: {}",
                providerMap.size(),
                providerMap.keySet());
    }

    /**
     * 根据提供商名称获取提供商
     * @param providerName 提供商名称
     * @return Optional包装的提供商
     */
    public Optional<SsoProvider> getProvider(String providerName) {
        return Optional.ofNullable(providerMap.get(providerName));
    }

    /**
     * 获取指定提供商，如果不存在则抛出异常
     * @param providerName 提供商名称
     * @return 提供商实例
     * @throws SsoAuthenticationException 提供商不存在时抛出
     */
    public SsoProvider getProviderOrThrow(String providerName) {
        return getProvider(providerName)
                .orElseThrow(() -> new SsoAuthenticationException(
                        providerName,
                        "PROVIDER_NOT_FOUND",
                        "SSO提供商不存在: " + providerName
                ));
    }

    /**
     * 获取所有已启用的提供商
     * @return 已启用的提供商列表
     */
    public List<SsoProvider> getEnabledProviders() {
        return providers.stream()
                .filter(SsoProvider::isEnabled)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有注册的提供商
     * @return 所有提供商列表
     */
    public List<SsoProvider> getAllProviders() {
        return Collections.unmodifiableList(providers);
    }

    /**
     * 检查提供商是否存在且已启用
     * @param providerName 提供商名称
     * @return true表示存在且已启用
     */
    public boolean isProviderEnabled(String providerName) {
        return getProvider(providerName)
                .map(SsoProvider::isEnabled)
                .orElse(false);
    }
}
