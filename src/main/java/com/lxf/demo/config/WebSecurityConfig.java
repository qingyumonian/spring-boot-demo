package com.lxf.demo.config;

import com.lxf.demo.security.encoder.Md5PasswordEncoder;
import com.lxf.demo.security.handler.FormAuthFailHandler;
import com.lxf.demo.security.handler.FormAuthSuccessHandler;
import com.lxf.demo.security.handler.JsonAuthenticationEntryPoint;
import com.lxf.demo.security.filter.TokenAuthenticationFilter;
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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  //无状态
                .and()
                .authorizeRequests()
                .antMatchers( "/api/auth/logout","/api/auth/**").permitAll()    //允许部分接口可以直接放回
                .anyRequest().authenticated()
                .and().formLogin().loginProcessingUrl("/api/auth/form")//设置登陆接口
                .successHandler(formAuthSuccessHandler).failureHandler(formAuthFailHandler) //设置登陆成功失败响应
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(jsonAuthenticationEntryPoint)   //设置异常响应
                .and()
                .addFilterAfter(tokenAuthenticationFilter, SecurityContextPersistenceFilter.class);  //在管理SecurityContext之后获取token 信息并放入全局中

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