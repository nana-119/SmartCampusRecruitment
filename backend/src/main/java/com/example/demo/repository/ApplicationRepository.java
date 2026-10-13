package com.example.demo.repository;

import com.example.demo.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findBySeekerPhoneAndStatus(String phone, Application.Status status);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.jobPosition jp " +
            "LEFT JOIN FETCH jp.recruiter " +
            "LEFT JOIN FETCH a.seeker")
    List<Application> findAllWithDetails();

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.jobPosition jp " +
            "LEFT JOIN FETCH jp.recruiter " +
            "LEFT JOIN FETCH a.seeker " +
            "WHERE a.seeker.phone = :phone")
    List<Application> findBySeekerPhoneWithDetails(@Param("phone") String phone);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.jobPosition jp " +
            "LEFT JOIN FETCH jp.recruiter " +
            "LEFT JOIN FETCH a.seeker " +
            "WHERE jp.recruiter.phone = :recruiterPhone")
    List<Application> findByJobPositionRecruiterPhoneWithDetails(@Param("recruiterPhone") String recruiterPhone);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.jobPosition jp " +
            "LEFT JOIN FETCH jp.recruiter " +
            "LEFT JOIN FETCH a.seeker " +
            "WHERE a.id = :id")
    Optional<Application> findByIdWithDetails(@Param("id") Integer id);

    @Query("SELECT COUNT(a) > 0 FROM Application a WHERE a.seeker.phone = :phone AND a.jobPosition.id = :jobId")
    boolean existsBySeekerPhoneAndJobPositionId(@Param("phone") String phone, @Param("jobId") Integer jobId);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.jobPosition jp " +
            "LEFT JOIN FETCH jp.recruiter " +
            "LEFT JOIN FETCH a.seeker " +
            "WHERE a.seeker.phone = :studentPhone AND a.jobPosition.id = :jobId")
    Optional<Application> findBySeekerPhoneAndJobPositionId(@Param("studentPhone") String studentPhone, @Param("jobId") Integer jobId);


    // 按岗位分组统计投递次数（用于投递量Top10）
    @Query("SELECT jp.title, COUNT(a) " +
            "FROM Application a " +
            "LEFT JOIN a.jobPosition jp " +  // 关联岗位表（Application -> JobPosition）
            "GROUP BY jp.title " +          // 按岗位名称分组
            "HAVING jp.title IS NOT NULL " + // 排除岗位名称为空的记录
            "ORDER BY COUNT(a) DESC")       // 按投递次数降序排序
    List<Object[]> countByPositionGroup();
}
