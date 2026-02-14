package com.lxf.demo.config;

import com.lxf.demo.config.sso.SsoProperties;
import com.lxf.demo.security.encoder.Md5PasswordEncoder;
import com.lxf.demo.security.handler.FormAuthFailHandler;
import com.lxf.demo.security.handler.FormAuthSuccessHandler;
import com.lxf.demo.security.handler.JsonAuthenticationEntryPoint;
import com.lxf.demo.security.filter.TokenAuthenticationFilter;
import com.lxf.demo.security.sso.cas.CasAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Resource
    private FormAuthSuccessHandler formAuthSuccessHandler;

    @Resource
    private FormAuthFailHandler formAuthFailHandler;

    @Resource
    private JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private Md5PasswordEncoder md5PasswordEncoder;

    @Resource
    private com.lxf.demo.security.handler.OidcAuthSuccessHandler oidcAuthSuccessHandler;

    @Resource
    private com.lxf.demo.security.handler.OidcAuthFailHandler oidcAuthFailHandler;

    @Resource
    private com.lxf.demo.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Resource
    private SsoProperties ssoProperties;

    /**
     * CAS过滤器 - 仅当sso.cas.enabled=true时注入
     */
    @Autowired(required = false)
    private CasAuthenticationFilter casAuthenticationFilter;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 设置用户密码规则，设置获取用户信息类
        auth.userDetailsService(userDetailsService).passwordEncoder(md5PasswordEncoder);
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  //无状态
                .and()
                .authorizeRequests()
                .antMatchers("/oauth2/**", "/login/oauth2/**","/login/**","/cas/**").permitAll()
                .antMatchers( "/api/auth/logout/**","/api/auth/**").permitAll()    //允许部分接口可以直接放回
                .anyRequest().authenticated()
                //基础的表单登陆
                .and().formLogin().loginProcessingUrl("/api/auth/form")//设置登陆接口
                .successHandler(formAuthSuccessHandler).failureHandler(formAuthFailHandler); //设置登陆成功失败响应

        // 条件配置Keycloak OIDC登录
        if (ssoProperties.getKeycloak().isEnabled()) {
            http.oauth2Login()
                    .authorizationEndpoint()
                        .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                        .and()
                    .redirectionEndpoint()
                        .baseUri("/api/auth/keycloak")
                        .and()
                    .successHandler(oidcAuthSuccessHandler)
                    .failureHandler(oidcAuthFailHandler);
        }

        http.exceptionHandling()
                .authenticationEntryPoint(jsonAuthenticationEntryPoint)   //设置异常响应
                .and()
                .addFilterAfter(tokenAuthenticationFilter, SecurityContextPersistenceFilter.class);  //在管理SecurityContext之后获取token 信息并放入全局中

        // CAS过滤器 - 仅当CAS启用时添加
        if (casAuthenticationFilter != null) {
            http.addFilterAfter(casAuthenticationFilter, TokenAuthenticationFilter.class);
        }

        http.headers().frameOptions().disable();
    }

    /**
     * 静态资源不做拦截
     * @param web
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/static/**", "/favicon.ico","/login.html")
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**");
    }
}