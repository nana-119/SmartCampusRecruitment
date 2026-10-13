package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="job_seeker")
public class Seeker implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "phone", length = 11, nullable = false)
    private String phone;

    @Column(name = "password", nullable = false, length = 12)
    private String password;

    @Column(name = "name", length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "ENUM('男','女')")
    private Gender gender;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(name = "student_number", length = 20)
    private String studentId;

    @Column(name = "hobbies", columnDefinition = "TEXT")
    private String hobbies;

    @Lob
    @Column(name = "resume_file")
    private byte[] resumeFile;

    @Lob
    @Column(name = "touxiang")
    private byte[] touxiang;


    // 枚举定义性别
    public enum Gender{
        男, 女
    }

    // 无参构造方法
    public Seeker() {
    }

    // 全参构造方法


    public Seeker(String phone, String password, String name, Gender gender, Date birthDate,String studentId, String hobbies, byte[] resumeFile, byte[] touxiang) {
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.studentId = studentId;
        this.hobbies = hobbies;
        this.resumeFile = resumeFile;
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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public byte[] getResumeFile() {
        return resumeFile;
    }

    public void setResumeFile(byte[] resumeFile) {
        this.resumeFile = resumeFile;
    }

    public byte[] getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(byte[] touxiang) {
        this.touxiang = touxiang;
    }
}
