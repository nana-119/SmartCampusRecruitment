package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService{
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public boolean verifyAdmin(String username, String password) {
        // 直接查询数据库中是否存在该用户名和密码的记录
        Admin admin = adminRepository.findByUsernameAndPassword(username, password);
        // 如果查询结果不为空，则验证通过
        return admin != null;
    }

    @Override
    public List<Admin> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins;
    }

    @Override
    public boolean existsByUsername(String username) {
        return adminRepository.findByUsername(username) != null;
    }

    @Override
    @Transactional
    public Admin createAdmin(Admin admin) {
        if (adminRepository.findByUsername(admin.getUsername()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        admin.setPassword(admin.getPassword());
        return adminRepository.save(admin);
    }

    @Override
    public Admin findById(Integer id) {
        Optional<Admin> admin = adminRepository.findById(id);
        return admin.orElse(null);
    }

    @Override
    public Admin findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return adminRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Admin update(Admin admin) {
        if (admin.getAdminId() == null) {
            throw new IllegalArgumentException("管理员ID不能为空");
        }

        Admin existingAdmin = adminRepository.findById(admin.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("管理员不存在，ID: " + admin.getAdminId()));
        existingAdmin.setUsername(admin.getUsername());
        existingAdmin.setPassword(admin.getPassword());
        return adminRepository.save(existingAdmin);
    }

    @Override
    public void delete(Integer id) {
        adminRepository.deleteById(id);
    }
}
