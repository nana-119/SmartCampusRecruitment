package com.example.demo.model;

import javax.persistence.*;
@Entity
@Table(name="admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer adminId;
    @Column(name = "username", length = 50, nullable = false)
    private String username;
    @Column(name = "password", nullable = false, length = 12)
    private String password;

    // 默认构造函数
    public Admin() {}

    // 带参数的构造函数
    public Admin(Integer adminId, String username, String password) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
    }

    // adminId 的 getter 和 setter 方法
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    // username 的 getter 和 setter 方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // password 的 getter 和 setter 方法
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
