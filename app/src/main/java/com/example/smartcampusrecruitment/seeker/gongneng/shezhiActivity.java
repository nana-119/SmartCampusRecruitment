package com.example.smartcampusrecruitment.seeker.gongneng;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;
import com.example.smartcampusrecruitment.seeker.MeActivity;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class shezhiActivity extends AppCompatActivity {
    private ImageView img_fh_shezhi,img_touxiang_shezhi;
    private EditText edit_name_shezhi,edit_birthdate_shezhi,edit_phone_shezhi,edit_hobbies_shezhi;
    private Button btn_save_shezhi;
    private Spinner spinner_gender_shezhi;
    private SeekerDTO user;
    private ArrayAdapter<CharSequence> adapter;
    private ApiService api = RetrofitClient.getInstance().create(ApiService.class);
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shezhi);
        initView();
        initData();
        onClick();
        registerPickImageLauncher();
    }

    private void registerPickImageLauncher() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            String base64 = uriToBase64(uri, 300);
                            user.setTouxiang(base64);
                            img_touxiang_shezhi.setImageBitmap(base64ToBitmap(base64));
                        } catch (IOException e) {
                            Toast.makeText(shezhiActivity.this, "处理图片失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void initView() {
        img_fh_shezhi=findViewById(R.id.img_fh_shezhi);
        img_touxiang_shezhi=findViewById(R.id.img_touxiang_shezhi);
        edit_name_shezhi=findViewById(R.id.edit_name_shezhi);
        edit_birthdate_shezhi=findViewById(R.id.edit_birthdate_shezhi);
        edit_phone_shezhi=findViewById(R.id.edit_phone_shezhi);
        edit_hobbies_shezhi=findViewById(R.id.edit_hobbies_shezhi);
        btn_save_shezhi=findViewById(R.id.btn_save_shezhi);
        spinner_gender_shezhi=findViewById(R.id.spinner_gender_shezhi);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender_shezhi.setAdapter(adapter);
    }

    private void initData() {
        String userPhone=getIntent().getStringExtra("studentPhone");
        if (TextUtils.isEmpty(userPhone)) {
            Toast.makeText(this, "参数为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        api.SeekerfindbyPhone(userPhone).enqueue(new Callback<SeekerDTO>() {
            @Override
            public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                if (response.isSuccessful() && response.body() != null){
                    user=response.body();
                    bindView();
                }else {
                    Toast.makeText(shezhiActivity.this,"当前用户获取失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SeekerDTO> call, Throwable t) {
                Toast.makeText(shezhiActivity.this,"网络错误: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void bindView() {
        edit_name_shezhi.setText(user.getName());
        edit_hobbies_shezhi.setText(user.getHobbies());
        edit_phone_shezhi.setText(user.getPhone());

        Date birth = user.getBirthDate();
        if (birth == null) birth = new Date();          // 防 null
        edit_birthdate_shezhi.setText(
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(birth)
        );

        int pos = adapter.getPosition(user.getGender());
        spinner_gender_shezhi.setSelection(Math.max(pos, 0));

        if (!TextUtils.isEmpty(user.getTouxiang())) {
            img_touxiang_shezhi.setImageBitmap(base64ToBitmap(user.getTouxiang()));
        }
    }


    private void onClick(){
        edit_birthdate_shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(
                        shezhiActivity.this,
                        (view, y, m, d) -> {
                            // 直接格式化成 yyyy-MM-dd
                            String date = String.format(Locale.CHINA, "%04d-%02d-%02d", y, m + 1, d);
                            edit_birthdate_shezhi.setText(date);
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });
        btn_save_shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setName(edit_name_shezhi.getText().toString());
                user.setGender(spinner_gender_shezhi.getSelectedItem().toString());
                try {
                    String str = edit_birthdate_shezhi.getText().toString();
                    Log.d("birth",str);
                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(str);
                    user.setBirthDate(date);
                } catch (ParseException e) {
                    Toast.makeText(shezhiActivity.this, "日期格式错误", Toast.LENGTH_SHORT).show();
                    return;   // 阻止后续保存
                }
                user.setHobbies(edit_hobbies_shezhi.getText().toString());
                String oldphone=user.getPhone();
                user.setPhone(edit_phone_shezhi.getText().toString());
                Log.d("phone",user.getPhone());
                api.updateSeeker(oldphone,user).enqueue(new Callback<SeekerDTO>() {
                    @Override
                    public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                        if (response.isSuccessful() && response.body() != null){
                            Toast.makeText(shezhiActivity.this,"修改成功！", Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor=getSharedPreferences("myinfo",0).edit();
                            editor.putString("phone",user.getPhone());
                            editor.commit();
                            Intent intent=new Intent(shezhiActivity.this, MeActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(shezhiActivity.this,"修改失败！", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SeekerDTO> call, Throwable t) {
                        Toast.makeText(shezhiActivity.this,"网络错误！", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        img_touxiang_shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String perm = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ? Manifest.permission.READ_MEDIA_IMAGES
                        : Manifest.permission.READ_EXTERNAL_STORAGE;

                if (ContextCompat.checkSelfPermission(shezhiActivity.this, perm) == PackageManager.PERMISSION_GRANTED) {
                    pickImageLauncher.launch("image/*");
                } else {
                    ActivityCompat.requestPermissions(shezhiActivity.this, new String[]{perm}, 2001);
                }
            }
        });
        img_fh_shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private String uriToBase64(Uri uri, int i) throws IOException {
        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        bmp = scaleBitmap(bmp, i);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private Bitmap scaleBitmap(Bitmap bmp, int i) {
        float ratio = (float) i / bmp.getWidth();
        int newH = (int) (bmp.getHeight() * ratio);
        return Bitmap.createScaledBitmap(bmp, i, newH, true);
    }

    private Bitmap base64ToBitmap(String touxiang) {
        byte[] bytes = Base64.decode(touxiang, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2001 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageLauncher.launch("image/*");
        } else {
            Toast.makeText(this, "需要权限才能选择头像", Toast.LENGTH_SHORT).show();
        }
    }
}