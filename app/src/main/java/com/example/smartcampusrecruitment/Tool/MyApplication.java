package com.example.smartcampusrecruitment.Tool;

import android.app.Application;

import com.example.smartcampusrecruitment.Adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;


public class MyApplication extends Application {
    private static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }

    public static Gson getGson() {
        return gson;
    }
}
