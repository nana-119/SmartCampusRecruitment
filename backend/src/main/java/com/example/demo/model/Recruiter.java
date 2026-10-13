package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "recruiter")
public class Recruiter implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 11, nullable = false)
    private String phone;

    @Column(nullable = false, length = 12)
    private String password;

    @Column(length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 100)
    private String company;

    @Column(length = 50)
    private String position;
    @Lob
    @Column(name = "touxiang")
    private byte[] touxiang;


    /* —— 枚举类型 —— */
    public enum Gender{
        男, 女
    }

    public Recruiter() {
    }

    public Recruiter(String phone, String password, String name, Gender gender, String company, String position, byte[] touxiang) {
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.company = company;
        this.position = position;
        this.touxiang = touxiang;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public byte[] getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(byte[] touxiang) {
        this.touxiang = touxiang;
    }
}
