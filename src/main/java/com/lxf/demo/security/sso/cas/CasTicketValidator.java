package com.lxf.demo.security.sso.cas;

import com.lxf.demo.config.sso.cas.CasProperties;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;

import java.util.Map;

/**
 * CAS 2.0 票据验证器
 * 使用 spring-security-cas 提供的 Cas20ServiceTicketValidator
 */
@Slf4j
public class CasTicketValidator {

    private final CasProperties casProperties;
    private final Cas20ServiceTicketValidator ticketValidator;

    public CasTicketValidator(CasProperties casProperties) {
        this.casProperties = casProperties;
        this.ticketValidator = new Cas30ServiceTicketValidator(casProperties.getServerUrlPrefix());
    }

    /**
     * 验证CAS票据
     *
     * @param ticket CAS Service Ticket (ST-xxxx)
     * @return CasUserInfo 用户信息，验证失败返回null
     */
    public CasUserInfo validateTicket(String ticket) {
        String serviceUrl = casProperties.getServiceUrl();
        log.info("验证CAS票据: ticket={}, service={}", ticket, serviceUrl);

        try {
            // 使用官方 Cas20ServiceTicketValidator 验证票据
            Assertion assertion = ticketValidator.validate(ticket, serviceUrl);

            if (assertion == null || assertion.getPrincipal() == null) {
                log.warn("CAS票据验证返回空断言");
                return null;
            }

            // 构建 CasUserInfo
            CasUserInfo userInfo = new CasUserInfo();
            userInfo.setUsername(assertion.getPrincipal().getName());

            // 提取用户属性
            Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
            if (attributes != null) {
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    Object value = entry.getValue();
                    if (value != null) {
                        userInfo.addAttribute(entry.getKey(), value.toString());
                    }
                }
            }

            log.info("CAS票据验证成功: username={}, attributes={}",
                    userInfo.getUsername(), userInfo.getAttributes());
            return userInfo;

        } catch (TicketValidationException e) {
            log.error("CAS票据验证失败: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("CAS票据验证异常", e);
            return null;
        }
    }
}
