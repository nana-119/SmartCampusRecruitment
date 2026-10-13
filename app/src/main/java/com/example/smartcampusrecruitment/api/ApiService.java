package com.example.smartcampusrecruitment.api;

import com.example.smartcampusrecruitment.model.dto.AcademicRecordDTO;
import com.example.smartcampusrecruitment.model.dto.ChatMessageDTO;
import com.example.smartcampusrecruitment.model.dto.ApplicationDTO;
import com.example.smartcampusrecruitment.model.dto.JobPositionDTO;
import com.example.smartcampusrecruitment.model.dto.RecruiterDTO;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @GET("seekers")
    Call<List<SeekerDTO>> getSeeker();

    @POST("seekers")
    Call<SeekerDTO> addSeeker(@Body SeekerDTO seekerDTO);

    @PUT("seekers/{phone}")
    Call<SeekerDTO> updateSeeker(@Path("phone") String phone, @Body SeekerDTO seekerDTO);

    @DELETE("seekers/{phone}")
    Call<Void> deleteSeeker(@Path("phone") String phone);

    @GET("seekers/findByPhone")
    Call<SeekerDTO> SeekerfindbyPhone(@Query("phone") String phone);

    @GET("recruiters")
    Call<List<RecruiterDTO>> getRecruiters();

    @POST("recruiters")
    Call<RecruiterDTO> createRecruiter(@Body RecruiterDTO recruiterDTO);

    @PUT("recruiters/{phone}")
    Call<RecruiterDTO> updateRecruiter(@Path("phone") String phone, @Body RecruiterDTO recruiterDTO);

    @DELETE("recruiters/{phone}")
    Call<Void> deleteSRecruiter(@Path("phone") String phone);
    @GET("recruiters/findByPhone")
    Call<RecruiterDTO> RecruiterfindbyPhone(@Query("phone") String phone);

    @GET("jobpositions")
    Call<List<JobPositionDTO>> getJobPosition();

    @GET("jobpositions/{id}")
    Call<JobPositionDTO> getJobPositionById(@Path("id") int id);
    @GET("messages")
    Call<List<ChatMessageDTO>> getHistoryMessages(
            @Query("user1Phone") String user1Phone,
            @Query("user2Phone") String user2Phone);

    @GET("messages/sender")
    Call<List<ChatMessageDTO>> getsenderMessages(
            @Query("senderPhone") String senderPhone);
    @POST("applications/createApplication")
    Call<ApplicationDTO> createApplication(@Body ApplicationDTO applicationDTO);

    @GET("applications/getAllApplications")
    Call<List<ApplicationDTO>> getAllApplications();

    @GET("applications/{phone}")
    Call<List<ApplicationDTO>> getApplicationByPhone(@Path("phone") String phone);

    @PUT("applications/{id}/status")
    Call<ApplicationDTO> updateApplicationStatus(@Path("id") int id, @Query("status") String status,
                                              @Query("reviewNotes") String reviewNotes);

    @DELETE("applications/{id}")
    Call<Void> deleteApplication(@Path("id") int id);

    @GET("academic-records/student")
    Call<List<AcademicRecordDTO>> getScoreBySchoolAndStudentId(
            @Query("graduationSchool") String graduationSchool,
            @Query("studentNumber") String studentNumber);
}
