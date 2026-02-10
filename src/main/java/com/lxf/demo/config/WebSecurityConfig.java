package com.lxf.demo.config;

import com.lxf.demo.security.handler.FormAuthFailHandler;
import com.lxf.demo.security.handler.FormAuthSuccessHandler;
import com.lxf.demo.security.provider.CustomAuthenticationProvider;
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

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Resource
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Resource
    private FormAuthSuccessHandler formAuthSuccessHandler;

    @Resource
    private FormAuthFailHandler formAuthFailHandler;


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定义认证提供者
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers( "/api/logout","/api/auth/**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginProcessingUrl("/api/auth/form")
                .successHandler(formAuthSuccessHandler).failureHandler(formAuthFailHandler)
                .and()
                .addFilterBefore(tokenAuthenticationFilter, SecurityContextPersistenceFilter.class);

        http.headers().frameOptions().disable();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/static/**", "/favicon.ico")
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**");
    }
}
