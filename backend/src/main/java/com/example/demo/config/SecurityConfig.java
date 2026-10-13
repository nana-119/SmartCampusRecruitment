package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // 允许登录页、登录验证路径匿名访问
                .antMatchers("/api/**", "/login", "/login/check").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .permitAll()
                .and()
                .csrf().disable(); // 保持CSRF禁用（与前端一致）
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 配置用户详情服务和明文编码器
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    // 明文密码编码器（仅测试用）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}
