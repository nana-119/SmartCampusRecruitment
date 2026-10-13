package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository; // 注入管理员数据库访问接口

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库查询管理员信息（根据用户名）
        Admin admin = adminRepository.findByUsername(username);

        // 2. 如果用户不存在，抛出异常（Spring Security会自动处理）
        if (admin == null) {
            throw new UsernameNotFoundException("用户名不存在：" + username);
        }

        // 3. 将数据库中的Admin对象转换为Spring Security需要的UserDetails对象
        // 注意：这里使用明文密码验证（与SecurityConfig中的NoOpPasswordEncoder匹配）
        return User.builder()
                .username(admin.getUsername()) // 用户名
                .password(admin.getPassword()) // 密码（数据库中存储的明文）
                .roles("ADMIN") // 赋予管理员角色（可根据实际需求调整）
                .build();
    }
}