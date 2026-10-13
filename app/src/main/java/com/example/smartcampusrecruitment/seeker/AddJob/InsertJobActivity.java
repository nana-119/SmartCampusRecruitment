package com.example.smartcampusrecruitment.seeker.AddJob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.seeker.Fragment.SalaryBottomSheet;
import com.example.smartcampusrecruitment.view.LoginActivity;

import java.util.HashSet;
import java.util.Set;

public class InsertJobActivity extends AppCompatActivity {
    private ImageView img_qwzw_xz,img_gzcs_xz,img_qwxz_xz,img_fh_xz;
    private TextView text_qwzw_xz,text_gzcs_xz,text_qwxz_xz;
    private Button btn_save_xz;
    private boolean isDataInitialized = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insert_job);
        initView();
        isDataInitialized=getIntent().getBooleanExtra("isDataInitialized",false);
        initData();
        btnSave();
        ImgOnclick();
    }
    private void initView(){
        img_qwzw_xz=findViewById(R.id.img_qwzw_xz);
        img_gzcs_xz=findViewById(R.id.img_gzcs_xz);
        img_qwxz_xz=findViewById(R.id.img_qwxz_xz);
        img_fh_xz=findViewById(R.id.img_fh_xz);
        text_qwzw_xz=findViewById(R.id.text_qwzw_xz);
        text_gzcs_xz=findViewById(R.id.text_gzcs_xz);
        text_qwxz_xz=findViewById(R.id.text_qwxz_xz);
        btn_save_xz=findViewById(R.id.btn_save_xz);
    }
    private void initData(){
        if (!isDataInitialized)
        {
            text_qwzw_xz.setText("");
            text_gzcs_xz.setText("");
            text_qwxz_xz.setText("");
            isDataInitialized=true;
        }else {
            text_qwzw_xz.setText(getSharedPreferences("jobinfo", 0).getString("qwzw", ""));
            Set<String> selectedCities = getSharedPreferences("jobinfo", 0)
                    .getStringSet("gzcs", new HashSet<>());
            String cities = "";
            for (String city : selectedCities) {
                if (!cities.isEmpty()) {
                    cities += "/";
                }
                cities += city;
                Log.d("city", city);
            }
            text_gzcs_xz.setText(cities);
            text_qwxz_xz.setText(
                    getSharedPreferences("jobinfo", 0)
                            .getString("expected_salary", "")
            );
        }
    }
    private void btnSave(){
        btn_save_xz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String position = text_qwzw_xz.getText().toString().trim();
                String cities   = text_gzcs_xz.getText().toString().trim();
                String salary   = text_qwxz_xz.getText().toString().trim();
                if (position.isEmpty() || cities.isEmpty() || salary.isEmpty()) {
                    Toast.makeText(InsertJobActivity.this, "请补全信息", Toast.LENGTH_SHORT).show();
                }
                // 1. 生成唯一 ID
                String key = "job_" + System.currentTimeMillis();

                // 2. 拼接成一条记录
                String record = position + "|" + cities + "|" + salary;
                getSharedPreferences("jobs", MODE_PRIVATE)
                        .edit()
                        .putString(key, record)
                        .apply();
                Intent intent=new Intent(InsertJobActivity.this, addJobActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }
    private void ImgOnclick(){
        img_qwzw_xz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InsertJobActivity.this, SelectJobActivity.class);
                startActivity(intent);
            }
        });
        img_gzcs_xz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InsertJobActivity.this, SelectCityActivity.class);
                startActivity(intent);
            }
        });
        img_qwxz_xz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InsertJobActivity.this,"点击成功",Toast.LENGTH_LONG).show();
                SalaryBottomSheet.newInstance(
                        range -> text_qwxz_xz.setText(range),   // range 已经是 "10K-25K"
                        InsertJobActivity.this
                ).show(getSupportFragmentManager(), "salary_sheet");
            }
        });
        img_fh_xz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InsertJobActivity.this, addJobActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}