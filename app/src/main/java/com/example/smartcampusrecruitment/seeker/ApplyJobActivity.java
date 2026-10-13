package com.example.smartcampusrecruitment.seeker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.dto.ApplicationDTO;
import com.example.smartcampusrecruitment.model.dto.JobPositionDTO;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;
import com.example.smartcampusrecruitment.seeker.Fragment.SalaryBottomSheet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyJobActivity extends AppCompatActivity {
    private ImageView img_fh_apply,img_qwxz_apply;
    private Button btn_select_jianli,btn_job_apply;
    private TextView text_jobtitle_apply,text_weizhi_apply,text_jinyan_apply,text_xueli_apply,text_salaryrange_apply;
    private EditText edit_jobIntention;
    private JobPositionDTO currentJob;
    private  SeekerDTO currentUser;
    private  String salary,intention;
    private ActivityResultLauncher<Intent> launcher;
    private ApplicationDTO newApplicationDTO = new ApplicationDTO();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apply_job);
        initView();
        initData();
        onClick();
        setupFileLauncher();
    }

    private void setupFileLauncher() {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        byte[] bytes = readFileToBytes(uri);
                        if (bytes != null) {
                            newApplicationDTO.setResumeFile(Base64.encodeToString(bytes, Base64.NO_WRAP | Base64.URL_SAFE));

                        }
                    }
                });
    }

    private void uploadToServer() {
        salary=text_salaryrange_apply.getText().toString();
        intention=edit_jobIntention.getText().toString();
        newApplicationDTO.setJobId(currentJob.getId());
        newApplicationDTO.setJobTitle(currentJob.getTitle());
        newApplicationDTO.setJobIntention(intention);
        newApplicationDTO.setExpectedSalary(salary);
        newApplicationDTO.setCompanyName(currentJob.getCompanyName());
        newApplicationDTO.setStudentPhone(currentUser.getPhone());
        newApplicationDTO.setStudentName(currentUser.getName());
        newApplicationDTO.setStatus("已投递");
        newApplicationDTO.setApplicationTime(LocalDateTime.now());

        if (TextUtils.isEmpty(newApplicationDTO.getResumeFile())) {
            String dbResume = currentUser.getResumeFile();
            if (!TextUtils.isEmpty(dbResume)) {
                newApplicationDTO.setResumeFile(dbResume);
            } else {
                Toast.makeText(this, "请先上传简历", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.createApplication(newApplicationDTO).enqueue(new Callback<ApplicationDTO>() {
            @Override
            public void onResponse(Call<ApplicationDTO> call, Response<ApplicationDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ApplyJobActivity.this, "简历已投递", Toast.LENGTH_SHORT).show();
                } else {
                    // 打印详细的错误信息
                    Log.e("UPLOAD", "HTTP " + response.code() + " " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.d("salary", salary);
                            Log.e("UPLOAD", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(ApplyJobActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApplicationDTO> call, Throwable t) {
                Log.e("UPLOAD", "Failure: " + t.getMessage());
                Toast.makeText(ApplyJobActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] readFileToBytes(Uri uri) {
        try (InputStream in = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "读取文件失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void onClick() {
        btn_select_jianli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // PDF / DOC / DOCX
                launcher.launch(intent);
            }
        });
        img_fh_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_qwxz_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalaryBottomSheet.newInstance(
                        range -> text_salaryrange_apply.setText(range),
                        ApplyJobActivity.this
                ).show(getSupportFragmentManager(),"salary_sheet");
            }
        });
        btn_job_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToServer();
            }
        });
    }

    private void initData() {
        currentJob= (JobPositionDTO) getIntent().getSerializableExtra("job");
        currentUser= (SeekerDTO) getIntent().getSerializableExtra("user");
        fetchUser();
        text_jobtitle_apply.setText(currentJob.getTitle());
        text_weizhi_apply.setText(currentJob.getWorkLocation());
        text_jinyan_apply.setText(currentJob.getExperienceRequirement());
        text_xueli_apply.setText(currentJob.getEducationRequirement());
        text_salaryrange_apply.setText("");
        edit_jobIntention.setText("");
    }

    private void fetchUser() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.SeekerfindbyPhone(currentUser.getPhone()).enqueue(new Callback<SeekerDTO>() {
            @Override
            public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                if (response.isSuccessful() && response.body() != null){
                    currentUser=response.body();
                }else{
                    Toast.makeText(ApplyJobActivity.this,
                            "用户数据获取失败: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SeekerDTO> call, Throwable t) {
                Toast.makeText(ApplyJobActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        img_fh_apply=findViewById(R.id.img_fh_apply);
        img_qwxz_apply=findViewById(R.id.img_qwxz_apply);
        btn_select_jianli=findViewById(R.id.btn_selectjianli_apply);
        btn_job_apply=findViewById(R.id.btn_job_apply);
        text_jobtitle_apply=findViewById(R.id.text_jobtitle_apply);
        text_weizhi_apply=findViewById(R.id.text_weizhi_apply);
        text_jinyan_apply=findViewById(R.id.text_jinyan_apply);
        text_xueli_apply=findViewById(R.id.text_xueli_apply);
        text_salaryrange_apply=findViewById(R.id.text_salaryrange_apply);
        edit_jobIntention=findViewById(R.id.edit_jobIntention);

    }
}