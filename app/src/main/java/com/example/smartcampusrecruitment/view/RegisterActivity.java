package com.example.smartcampusrecruitment.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.dto.RecruiterDTO;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;
import com.example.smartcampusrecruitment.recruiter.RecruiterMainActivity;
import com.example.smartcampusrecruitment.seeker.SeekerMainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_phone_rg,et_pwd_rg;
    private Button btn_rgandlg_rg,btn_cancle_rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        initView();
        btnRgAndLg();
        btnCancle();
    }
    private void initView(){
        et_phone_rg=findViewById(R.id.et_phone_rg);
        et_pwd_rg=findViewById(R.id.et_pwd_rg);
        btn_rgandlg_rg=findViewById(R.id.bt_registerandlogin_rg);
        btn_cancle_rg=findViewById(R.id.bt_cancle_rg);
    }

    private void tiaozhuan(){
        int role=getIntent().getIntExtra("role",0);
        if (role==1)
        {
            Intent intent=new Intent(RegisterActivity.this, SeekerMainActivity.class);
            startActivity(intent);
            finish();
        }else if (role==2){
            Intent intent=new Intent(RegisterActivity.this, RecruiterMainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void btnRgAndLg(){
        btn_rgandlg_rg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=et_phone_rg.getText().toString();
                String pwd=et_pwd_rg.getText().toString();
                ApiService api = RetrofitClient.getInstance().create(ApiService.class);

                if (phone.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "请填写正确的手机号", Toast.LENGTH_LONG).show();
                } else if (pwd.length() == 0 || !pwd.matches("^(?=.*[a-zA-Z])(?=.*\\\\d)[a-zA-Z\\\\d]{6,12}$")) {
                    Toast.makeText(RegisterActivity.this, "密码格式不符合", Toast.LENGTH_LONG).show();
                } else {
                    int role = getIntent().getIntExtra("role", 0);
                    if (role == 1) {
                        SeekerDTO seekerDTO = new SeekerDTO();
                        seekerDTO.setPhone(phone);
                        seekerDTO.setPassword(pwd);
                        api.addSeeker(seekerDTO).enqueue(new Callback<SeekerDTO>() {
                            @Override
                            public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    tiaozhuan();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<SeekerDTO> call, Throwable t) {
                                Log.e("API", "网络错误", t);
                            }
                        });
                    } else if (role == 2) {
                        // 假设你也有一个 RecruiterDTO 类
                        RecruiterDTO recruiterDTO = new RecruiterDTO();
                        recruiterDTO.setPhone(phone);
                        recruiterDTO.setPassword(pwd);

                        api.createRecruiter(recruiterDTO).enqueue(new Callback<RecruiterDTO>() {
                            @Override
                            public void onResponse(Call<RecruiterDTO> call, Response<RecruiterDTO> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    tiaozhuan();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<RecruiterDTO> call, Throwable t) {
                                Log.e("API", "网络错误", t);
                            }
                        });
                    }
                }
            }
        });
    }

    private void btnCancle(){
        btn_cancle_rg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int role=getIntent().getIntExtra("role",0);
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("role",role);
                startActivity(intent);
                finish();
            }
        });
    }
}