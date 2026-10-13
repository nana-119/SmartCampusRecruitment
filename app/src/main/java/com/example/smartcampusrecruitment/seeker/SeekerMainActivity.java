package com.example.smartcampusrecruitment.seeker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.seeker.AddJob.addJobActivity;
import com.example.smartcampusrecruitment.seeker.Fragment.FragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeekerMainActivity extends AppCompatActivity {
    private TabLayout jobTab_zw;
    private ViewPager2 viewPager;
    private ImageView sousuo_zw,jiahao_zw,image_zhiwei_zw,image_toudi_zw,image_xiaoxi_zw,image_me_zw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_main);
        initView();
        initData();
        initClick();

    }
    private void initView(){
        jobTab_zw=findViewById(R.id.jobTab_zw);
        sousuo_zw=findViewById(R.id.sousuo_zw);
        jiahao_zw=findViewById(R.id.jiahao_zw);
        image_zhiwei_zw=findViewById(R.id.image_zhiwei_zw);
        image_toudi_zw=findViewById(R.id.image_toudi_zw);
        image_xiaoxi_zw=findViewById(R.id.image_xiaoxi_zw);
        image_me_zw=findViewById(R.id.image_me_zw);
        viewPager=findViewById(R.id.viewpager);
    }
    private void initData(){
        SharedPreferences sp = getSharedPreferences("jobs", MODE_PRIVATE);
        Map<String, ?> allJobs = sp.getAll(); // 读取所有 job_ 开头的键值对

        List<String> titles = new ArrayList<>();
        List<String> cities=new ArrayList<>();
        List<String> salaries = new ArrayList<>();
        titles.add("综合");
        cities.add("");
        salaries.add("");
        for (Map.Entry<String, ?> entry : allJobs.entrySet()) {
            if (entry.getKey().startsWith("job_")) {
                String value = (String) entry.getValue();
                String[] parts = value.split("\\|");
                if (parts.length >= 1) {
                    titles.add(parts[0]);         // 标题（如“前端开发工程师”）
                    cities.add(parts[1]);
                    salaries.add(parts[2]);
                }
            }
        }

        // 1. 动态添加 Tab
        for (String title : titles) {
            jobTab_zw.addTab(jobTab_zw.newTab().setText(title));
        }

        // 2. 设置 Adapter
        FragmentAdapter fragmentAdapter = new FragmentAdapter(this,titles,cities,salaries,"zhiwei");
        viewPager.setAdapter(fragmentAdapter);

        // 3. 关联 TabLayout 与 ViewPager
        new TabLayoutMediator(jobTab_zw, viewPager, (tab, position) -> {
            tab.setText(titles.get(position));
        }).attach();

    }
    private void initClick() {
        image_zhiwei_zw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        image_toudi_zw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SeekerMainActivity.this, ToudiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        image_xiaoxi_zw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SeekerMainActivity.this, xiaoxiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        image_me_zw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SeekerMainActivity.this, MeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        sousuo_zw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SeekerMainActivity.this, sousuoActivity.class);
                startActivity(intent);
            }
        });
        jiahao_zw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SeekerMainActivity.this, addJobActivity.class);
                startActivity(intent);
            }
        });
    }
}