package com.example.smartcampusrecruitment.seeker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.seeker.Fragment.FragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class ToudiActivity extends AppCompatActivity {
    private TabLayout jobTab_td;
    private ViewPager2 viewpager_td;
    private ImageView image_zhiwei_td, image_toudi_td, image_xiaoxi_td, image_me_td;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_toudi);
        initView();
        initData();
        initClick();
    }

    private void initClick() {
        image_zhiwei_td.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ToudiActivity.this,SeekerMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        image_toudi_td.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        image_xiaoxi_td.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ToudiActivity.this, xiaoxiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        image_me_td.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ToudiActivity.this, MeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initData() {
        List<String> titles = Arrays.asList("已投递", "已通过", "已拒绝");
        List<String> cities  = Arrays.asList("", "", "");   // 不筛选城市
        List<String> salaries = Arrays.asList("", "", "");  // 不筛选薪资
        for (String title : titles) {
            jobTab_td.addTab(jobTab_td.newTab().setText(title));
        }

        // 2. 设置 Adapter
        FragmentAdapter fragmentAdapter = new FragmentAdapter(this,titles,cities,salaries,"Application");
        viewpager_td.setAdapter(fragmentAdapter);

        // 3. 关联 TabLayout 与 ViewPager
        new TabLayoutMediator(jobTab_td, viewpager_td, (tab, position) -> {
            tab.setText(titles.get(position));
        }).attach();
    }

    private void initView() {
        jobTab_td = findViewById(R.id.jobTab_td);
        image_zhiwei_td = findViewById(R.id.image_zhiwei_td);
        image_toudi_td = findViewById(R.id.image_toudi_td);
        image_xiaoxi_td = findViewById(R.id.image_xiaoxi_td);
        image_me_td = findViewById(R.id.image_me_td);
        viewpager_td = findViewById(R.id.viewpager_td);
    }
}
