package com.example.smartcampusrecruitment.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText et_phone_lg,et_pwd_lg;
    private CheckBox check_reme;
    private Button btn_login,bt_newregister_lg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        initView();
        btnLogin();
        btnNewRegister();
        displayinfo();
    }
    private void initView(){
        et_phone_lg=findViewById(R.id.et_phone_lg);
        et_pwd_lg=findViewById(R.id.et_pwd_lg);
        btn_login=findViewById(R.id.bt_login_lg);
        bt_newregister_lg=findViewById(R.id.bt_newregister_lg);
        check_reme=findViewById(R.id.check_reme);
    }
    private void tiaozhuan(){
        int role=getIntent().getIntExtra("role",0);
        SharedPreferences.Editor editor=getSharedPreferences("myinfo",0).edit();
        editor.putString("phone",et_phone_lg.getText().toString());
        editor.putString("pwd",et_pwd_lg.getText().toString());
        editor.putBoolean("st",check_reme.isChecked());
        editor.commit();
        if (role==1)
        {
            Intent intent=new Intent(LoginActivity.this, SeekerMainActivity.class);
            startActivity(intent);
            finish();
        }else if (role==2){
            Intent intent=new Intent(LoginActivity.this, RecruiterMainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void btnLogin(){
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int role=getIntent().getIntExtra("role",0);
                ApiService api = RetrofitClient.getInstance().create(ApiService.class);

                String phone = et_phone_lg.getText().toString();
                String pwd = et_pwd_lg.getText().toString();

                if (phone.isEmpty() || pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "手机号或密码为空", Toast.LENGTH_LONG).show();
                } else if (phone.length() != 11) {
                    Toast.makeText(LoginActivity.this, "请填写正确的手机号", Toast.LENGTH_LONG).show();
                } else {
                    if (role == 1) {
                        api.getSeeker().enqueue(new Callback<List<SeekerDTO>>() {
                            @Override
                            public void onResponse(Call<List<SeekerDTO>> call, Response<List<SeekerDTO>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    boolean userFound = false;
                                    List<SeekerDTO> userList = response.body();
                                    for (SeekerDTO u : userList) {
                                        if (phone.equals(u.getPhone())) {
                                            if (pwd.equals(u.getPassword())) {
                                                Log.d("API", "用户：" + u.getName() + " 电话：" + u.getPhone());
                                                userFound = true;
                                                tiaozhuan();
                                                break;
                                            } else {
                                                Log.d("api", "电话：" + u.getPhone() + " 密码：" + u.getPassword());
                                                Toast.makeText(LoginActivity.this, "手机号或密码错误", Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                        }
                                    }
                                    if (!userFound) {
                                        Toast.makeText(LoginActivity.this, "未注册", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<SeekerDTO>> call, Throwable t) {
                                Log.e("API", "网络错误", t);
                            }
                        });
                    } else if (role == 2) {
                        api.getRecruiters().enqueue(new Callback<List<RecruiterDTO>>() {
                            @Override
                            public void onResponse(Call<List<RecruiterDTO>> call, Response<List<RecruiterDTO>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    boolean userFound = false;
                                    List<RecruiterDTO> userList = response.body();
                                    for (RecruiterDTO u : userList) {
                                        if (phone.equals(u.getPhone())) {
                                            if (pwd.equals(u.getPassword())) {
                                                Log.d("API", "用户：" + u.getName() + " 电话：" + u.getPhone());
                                                userFound = true;
                                                tiaozhuan();
                                                break;
                                            } else {
                                                Log.d("api", "电话：" + u.getPhone() + " 密码：" + u.getPassword());
                                                Toast.makeText(LoginActivity.this, "手机号或密码错误", Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                        }
                                    }
                                    if (!userFound) {
                                        Toast.makeText(LoginActivity.this, "未注册", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<RecruiterDTO>> call, Throwable t) {
                                Log.e("API", "网络错误", t);
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "未登录", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    private void btnNewRegister(){
        bt_newregister_lg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int role=getIntent().getIntExtra("role",0);
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("role",role);
                startActivity(intent);
                finish();
            }
        });
    }
    private void displayinfo(){
        String strphone=getSharedPreferences("myinfo",0).getString("phone","");
        String strpwd=getSharedPreferences("myinfo",0).getString("pwd","");
        Boolean status=getSharedPreferences("myinfo",0).getBoolean("st",false);
        if(status==true){
            et_phone_lg.setText(strphone);
            et_pwd_lg.setText(strpwd);
            check_reme.setChecked(true);
        }else{
            et_phone_lg.setText("");
            et_pwd_lg.setText("");
            check_reme.setChecked(false);
        }
    }
}