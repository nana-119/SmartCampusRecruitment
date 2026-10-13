package com.example.smartcampusrecruitment.seeker.AddJob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.seeker.Fragment.SalaryBottomSheet;

public class UpdateJobActivity extends AppCompatActivity {
    private ImageView img_qwzw_bj,img_gzcs_bj,img_qwxz_bj,img_fh_bj;
    private TextView text_qwzw_bj,text_gzcs_bj,text_qwxz_bj;
    private Button btn_save_bj;
    private String oldKey;
    private boolean isDataInitialized = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_job);
        initView();
        initData();
        btnSave();
        ImgOnclick();
        onResume();

    }
    private void initView(){
        img_qwzw_bj=findViewById(R.id.img_qwzw_bj);
        img_gzcs_bj=findViewById(R.id.img_gzcs_bj);
        img_qwxz_bj=findViewById(R.id.img_qwxz_bj);
        img_fh_bj=findViewById(R.id.img_fh_bj);
        text_qwzw_bj=findViewById(R.id.text_qwzw_bj);
        text_gzcs_bj=findViewById(R.id.text_gzcs_bj);
        text_qwxz_bj=findViewById(R.id.text_qwxz_bj);
        btn_save_bj=findViewById(R.id.btn_save_bj);
    }
    private void initData(){
        if (isDataInitialized) return;
        String position = getIntent().getStringExtra("position");
        String cs = getIntent().getStringExtra("cities");
        String salary = getIntent().getStringExtra("salary");
        text_qwzw_bj.setText(position);
        text_gzcs_bj.setText(cs);
        text_qwxz_bj.setText(salary);
        isDataInitialized = true;
    }
    private void ImgOnclick(){
        ActivityResultLauncher<Intent> jobPickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                String job = result.getData().getStringExtra("qwzw");
                                text_qwzw_bj.setText(job);
                                Log.d("newp",job);
                            }
                        });
        img_qwzw_bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateJobActivity.this, SelectJobActivity.class);
                intent.putExtra("node",1);
                jobPickerLauncher.launch(intent);
            }
        });
        ActivityResultLauncher<Intent> cityPickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                String cities = result.getData().getStringExtra("gzcs");
                                text_gzcs_bj.setText(cities);
                                Log.d("newc",cities);
                            }
                        });
        img_gzcs_bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateJobActivity.this, SelectCityActivity.class);
                intent.putExtra("node",1);
                cityPickerLauncher.launch(intent);
            }
        });
        img_qwxz_bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalaryBottomSheet.newInstance(
                        range -> text_qwxz_bj.setText(range),   // range 已经是 "10K-25K"
                        UpdateJobActivity.this
                ).show(getSupportFragmentManager(), "salary_sheet");
            }
        });
        img_fh_bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateJobActivity.this, addJobActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void btnSave(){
        btn_save_bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldKey=getIntent().getStringExtra("key");
                String newp=text_qwzw_bj.getText().toString();
                String newc=text_gzcs_bj.getText().toString();
                String news=text_qwxz_bj.getText().toString();
                String record = newp + "|" + newc + "|" + news;
                Log.d("newr",record);
                Log.d("oldkey",oldKey);
                SharedPreferences.Editor editor=getSharedPreferences("jobs",MODE_PRIVATE).edit();
                editor.putString(oldKey,record);
                try {
                    editor.apply();
                    Toast.makeText(UpdateJobActivity.this, "编辑成功", Toast.LENGTH_LONG).show();
                    finish();
                } catch (Exception e) {
                    Log.e("SharedPreferences", "写入失败", e);
                    Toast.makeText(UpdateJobActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!isDataInitialized){
            String position = getIntent().getStringExtra("position");
            String cs = getIntent().getStringExtra("cities");
            String salary = getIntent().getStringExtra("salary");
            text_qwzw_bj.setText(position);
            text_gzcs_bj.setText(cs);
            text_qwxz_bj.setText(salary);
        }
    }
}