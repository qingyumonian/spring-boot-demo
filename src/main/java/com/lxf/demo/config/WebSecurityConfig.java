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
        // 使用默认认证机制
        auth.userDetailsService(userDetailsService).passwordEncoder(md5PasswordEncoder);
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
                .antMatchers( "/api/auth/logout","/api/auth/**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginProcessingUrl("/api/auth/form")
                .successHandler(formAuthSuccessHandler).failureHandler(formAuthFailHandler)
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                .and()
                .addFilterBefore(tokenAuthenticationFilter, SecurityContextPersistenceFilter.class);

        http.headers().frameOptions().disable();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/static/**", "/favicon.ico","/login.html")
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**");
    }
}