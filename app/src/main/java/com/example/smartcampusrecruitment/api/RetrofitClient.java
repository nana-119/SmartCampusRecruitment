package com.example.smartcampusrecruitment.api;

import com.example.smartcampusrecruitment.Tool.MyApplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.31.122:8081/api/"; // 本地调试
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(MyApplication.getGson()))
                    .build();
        }
        return retrofit;
    }
}
