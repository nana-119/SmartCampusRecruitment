package com.example.smartcampusrecruitment.seeker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.dto.ApplicationDTO;
import com.example.smartcampusrecruitment.model.dto.JobPositionDTO;
import com.example.smartcampusrecruitment.model.dto.RecruiterDTO;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobDetailActivity extends AppCompatActivity {
    TextView text_title_jd,text_salary_jd,text_weizhi_jd,text_jinyan_jd,text_xueli_jd,
            text_recruiter_jd,text_gsandzw_jd,text_salarydetail_jd,text_jobdetail_jd;
    ImageView img_fh_jd,img_soucang_jd;
    LinearLayout layout_benefit_jd;
    Button btn_lianxi_jd,btn_apply_jd;
    JobPositionDTO currentJob;
    SeekerDTO currentUser;
    RecruiterDTO recruiter;
    SharedPreferences spCollect;
    private static final int REQUEST_CODE = 1;
    private ActivityResultLauncher<Intent> launcher;
    private ApplicationDTO newApplicationDTO = new ApplicationDTO();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_detail);
        spCollect = getSharedPreferences("collect", MODE_PRIVATE);
        initView();
        initData();
        onClick();
    }

    private void onClick() {
        btn_lianxi_jd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobDetailActivity.this, ChatActivity.class);
                intent.putExtra("CURRENT_USER_PHONE", currentUser.getPhone());
                intent.putExtra("OTHER_USER_PHONE", recruiter.getPhone());
                intent.putExtra("IS_RECRUITER", false);
                String show = recruiter.getCompany() + "·" + recruiter.getPosition() + "·" + recruiter.getName();
                intent.putExtra("recruiter", show);
                startActivity(intent);
            }
        });
        btn_apply_jd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobDetailActivity.this,ApplyJobActivity.class);
                intent.putExtra("job",currentJob);
                intent.putExtra("user",currentUser);
                startActivity(intent);

            }
        });
        img_fh_jd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_soucang_jd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentJob == null) return;
                // 切换收藏状态
                boolean isCollected = toggleCollectStatus(currentJob.getId());
                updateCollectIcon(isCollected);

                String msg = isCollected ? "已收藏" : "已取消收藏";
                Toast.makeText(JobDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        JobPositionDTO intentJob = (JobPositionDTO) getIntent().getSerializableExtra("job");
        int biaoshi=getIntent().getIntExtra("biaoshi",0);
        if (intentJob == null) {
            Toast.makeText(this, "职位信息缺失", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String phone = getSharedPreferences("myinfo", 0).getString("phone", "");
        currentUser = new SeekerDTO();
        currentUser.setPhone(phone);
        if (intentJob.getId() == null) {
            // 无 id → 直接渲染已有数据
            currentJob = intentJob;
            renderUI();
        } else {
            // 有 id → 拉最新数据再渲染
            fetchJobDetail(intentJob.getId());
        }
        if (biaoshi==1){
            btn_apply_jd.setText("已发布求职申请");
            btn_apply_jd.setEnabled(false);
            btn_apply_jd.setAlpha(0.5f);
        }
    }

    private void fetchJobDetail(int id) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getJobPositionById(id).enqueue(new Callback<JobPositionDTO>() {
            @Override
            public void onResponse(Call<JobPositionDTO> call, Response<JobPositionDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentJob = response.body();
                    renderUI();
                } else {
                    Toast.makeText(JobDetailActivity.this,
                            "数据获取失败: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JobPositionDTO> call, Throwable t) {
                Toast.makeText(JobDetailActivity.this,
                        "网络请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderUI() {
        if (currentJob == null) return;

        // 职位信息
        text_title_jd.setText(currentJob.getTitle());
        text_salary_jd.setText(currentJob.getSalary());
        text_weizhi_jd.setText(currentJob.getWorkLocation());
        text_jinyan_jd.setText(currentJob.getExperienceRequirement());
        text_xueli_jd.setText(currentJob.getEducationRequirement());
        text_jobdetail_jd.setText(currentJob.getDescription());

        // 招聘者信息
        recruiter = new RecruiterDTO();
        recruiter.setName(currentJob.getRecruiterName());
        recruiter.setCompany(currentJob.getCompanyName());
        recruiter.setPhone(currentJob.getRecruiterPhone());
        recruiter.setPosition(currentJob.getPosition());
        text_recruiter_jd.setText(recruiter.getName());
        text_gsandzw_jd.setText(recruiter.getCompany() + "·" + recruiter.getPosition());

        // 薪资 / 福利 / 收藏
        String range = currentJob.getSalary();
        if (range == null || !range.contains("-")) {
            text_salarydetail_jd.setText("薪资范围：" + range);
        } else {
            String[] parts = range.toLowerCase().replace("k", "").split("-");
            int min = Integer.parseInt(parts[0]) * 1000;
            int max = Integer.parseInt(parts[1]) * 1000;
            text_salarydetail_jd.setText("薪资范围：" + min + "-" + max + "元/月");
        }
        layout_benefit_jd.removeAllViews();
        String benefits = currentJob.getBenefits();
        if (benefits != null) {
            List<String> tags = Arrays.asList(benefits.split("、"));
            for (String tag : tags) {
                TextView tv = new TextView(this);
                tv.setText(tag);
                tv.setTextSize(14);
                tv.setPadding(16, 8, 16, 8);
                tv.setBackgroundResource(R.drawable.bg_round_top);
                tv.setTextColor(Color.BLACK);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMarginEnd(12);
                tv.setLayoutParams(lp);
                layout_benefit_jd.addView(tv);
            }
        }
        updateCollectIcon(spCollect.getBoolean(String.valueOf(currentJob.getId()), false));
    }

    private void initView() {
        text_title_jd=findViewById(R.id.text_title_jd);
        text_salary_jd=findViewById(R.id.text_salary_jd);
        text_weizhi_jd=findViewById(R.id.text_weizhi_jd);
        text_jinyan_jd=findViewById(R.id.text_jinyan_jd);
        text_xueli_jd=findViewById(R.id.text_xueli_jd);
        text_recruiter_jd=findViewById(R.id.text_recruiter_jd);
        text_gsandzw_jd=findViewById(R.id.text_gsandzw_jd);
        text_salarydetail_jd=findViewById(R.id.text_salarydetail_jd);
        text_jobdetail_jd=findViewById(R.id.text_jobdetail_jd);
        img_fh_jd=findViewById(R.id.img_fh_jd);
        img_soucang_jd=findViewById(R.id.img_soucang_jd);
        layout_benefit_jd=findViewById(R.id.layout_benefit_jd);
        btn_lianxi_jd=findViewById(R.id.btn_lianxi_jd);
        btn_apply_jd=findViewById(R.id.btn_apply_jd);

    }
    private boolean toggleCollectStatus(int jobId) {
        boolean current = spCollect.getBoolean(String.valueOf(jobId), false);
        spCollect.edit().putBoolean(String.valueOf(jobId), !current).apply();
        return !current;
    }

    private void updateCollectIcon(boolean isCollected) {
        img_soucang_jd.setImageResource(
                isCollected ? R.drawable.soucang1 : R.drawable.soucang
        );
    }
}