package com.example.demo.service;

import com.example.demo.model.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    boolean verifyAdmin(String username, String password);
    List<Admin> getAllAdmins();
    boolean existsByUsername(String username);
    Admin createAdmin(Admin admin);
    Admin findById(Integer id);
    Admin findByUsername(String username);
    Admin update(Admin admin);
    void delete(Integer id);
}
