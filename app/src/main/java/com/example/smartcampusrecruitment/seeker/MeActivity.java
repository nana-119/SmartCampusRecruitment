package com.example.smartcampusrecruitment.seeker;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;
import com.example.smartcampusrecruitment.seeker.gongneng.chengjiActivity;
import com.example.smartcampusrecruitment.seeker.gongneng.shezhiActivity;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeActivity extends AppCompatActivity {
    private static final String TAG = "MeActivity";
    private static final String PREVIEW_CACHE_DIR = "preview_cache";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private ImageView img_touxiang_me,img_zhiwei_me,img_toudi_me,img_xiaoxi_me,img_me_me,img_forget_me;
    private TextView text_name_me;
    private Button btn_chengji,btn_bangding,btn_jiebang,btn_shangchuan,btn_yulan,btn_shezhi;
    private SeekerDTO currentUser;
    private ApiService api = RetrofitClient.getInstance().create(ApiService.class);;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_me);
        initView();
        initData();
        setupFilePicker();
        setupClickListeners();
        cleanOldCacheFiles();
    }

    private void initView() {
        img_touxiang_me=findViewById(R.id.img_touxiang_me);
        img_zhiwei_me=findViewById(R.id.image_zhiwei_me);
        img_toudi_me=findViewById(R.id.image_toudi_me);
        img_xiaoxi_me=findViewById(R.id.image_xiaoxi_me);
        img_me_me=findViewById(R.id.image_me_me);
        img_forget_me=findViewById(R.id.img_forget_me);
        text_name_me=findViewById(R.id.text_name_me);
        btn_chengji=findViewById(R.id.btn_chengji_me);
        btn_bangding=findViewById(R.id.btn_bangding_me);
        btn_jiebang=findViewById(R.id.btn_jiebang_me);
        btn_shangchuan=findViewById(R.id.btn_shangchuan_me);
        btn_yulan=findViewById(R.id.btn_yulan_me);
        btn_shezhi=findViewById(R.id.btn_shezhi_me);
    }

    private void initData() {
        SharedPreferences sp = getSharedPreferences("myinfo", MODE_PRIVATE);
        String phone = sp.getString("phone", "");
        currentUser=new SeekerDTO();
        currentUser.setPhone(phone);
        api.SeekerfindbyPhone(currentUser.getPhone()).enqueue(new Callback<SeekerDTO>() {
            @Override
            public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                if (response.isSuccessful() && response.body() != null){
                    currentUser=response.body();
                    updateUI();
                }else {
                    Toast.makeText(MeActivity.this,"当前用户获取失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SeekerDTO> call, Throwable t) {
                Toast.makeText(MeActivity.this,"网络错误: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI() {
        text_name_me.setText(currentUser.getName());

        // 处理头像
        String base64 = currentUser.getTouxiang();
        if (!TextUtils.isEmpty(base64)) {
            try {
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    img_touxiang_me.setImageBitmap(bitmap);
                    return;
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "头像Base64解码失败", e);
            }
        }
        img_touxiang_me.setImageResource(R.drawable.touxiang);
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        handleFileSelection(uri);
                    }
                });
    }

    private void handleFileSelection(Uri uri) {
        try {
            // 检查文件大小
            long fileSize = getFileSize(uri);
            if (fileSize > MAX_FILE_SIZE) {
                showToast("文件大小不能超过10MB");
                return;
            }

            // 读取文件
            byte[] fileBytes = readFileToBytes(uri);
            if (fileBytes == null || fileBytes.length == 0) {
                showToast("文件读取失败");
                return;
            }

            // 验证文件类型
            String mimeType = getMimeTypeFromBytes(fileBytes);
            if (!isSupportedFileType(mimeType)) {
                showToast("不支持的文件类型");
                return;
            }

            // 更新用户数据
            currentUser.setResumeFile(Base64.encodeToString(fileBytes, Base64.NO_WRAP));
            uploadResumeToServer();

        } catch (SecurityException e) {
            Log.e(TAG, "文件访问权限错误", e);
            showToast("无法访问文件");
        } catch (Exception e) {
            Log.e(TAG, "文件处理错误", e);
            showToast("文件处理失败");
        }
    }

    private long getFileSize(Uri uri) throws IOException {
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            return in.available();
        }
    }

    private byte[] readFileToBytes(Uri uri) throws IOException {
        try (InputStream in = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        }
    }

    private void uploadResumeToServer() {
        showToast("正在上传简历...");

        api.updateSeeker(currentUser.getPhone(), currentUser).enqueue(new Callback<SeekerDTO>() {
            @Override
            public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                if (response.isSuccessful()) {
                    showToast("简历上传成功");
                    initData(); // 刷新数据
                } else {
                    showToast("简历上传失败");
                }
            }

            @Override
            public void onFailure(Call<SeekerDTO> call, Throwable t) {
                Log.e(TAG, "简历上传网络错误", t);
                showToast("网络错误: " + t.getMessage());
            }
        });
    }

    private void setupClickListeners() {
        btn_chengji.setOnClickListener(v -> {
            if (TextUtils.isEmpty(currentUser.getStudentId())) {
                showToast("未绑定学号");
            } else {
                Intent intent=new Intent(this, chengjiActivity.class);
                intent.putExtra("studentId",currentUser.getStudentId());
                intent.putExtra("school",currentUser.getGraduationSchool());
                startActivity(intent);
            }
        });

        btn_bangding.setOnClickListener(v -> showBindDialog());
        btn_jiebang.setOnClickListener(v -> showUnbindDialog());
        btn_shangchuan.setOnClickListener(v -> openFilePicker());
        btn_yulan.setOnClickListener(v -> previewResume());
        btn_shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MeActivity.this, shezhiActivity.class);
                intent.putExtra("studentPhone",currentUser.getPhone());
                startActivity(intent);
            }
        });
        img_zhiwei_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MeActivity.this, SeekerMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        img_toudi_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MeActivity.this,ToudiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        img_xiaoxi_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MeActivity.this, xiaoxiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        img_me_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        img_forget_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangepwdDialog();
            }
        });
    }

    private void showChangepwdDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        final EditText editOldPassword = dialogView.findViewById(R.id.edit_old_password);
        final EditText editNewPassword = dialogView.findViewById(R.id.edit_new_password);
        final EditText editConfirmNewPassword = dialogView.findViewById(R.id.edit_confirm_new_password);

        new AlertDialog.Builder(this)
                .setTitle("修改密码")
                .setView(dialogView)
                .setPositiveButton("确定", (dialog, which) -> {
                    String oldPassword = editOldPassword.getText().toString().trim();
                    String newPassword = editNewPassword.getText().toString().trim();
                    String confirmNewPassword = editConfirmNewPassword.getText().toString().trim();

                    // 验证原密码（这里假设你有一个方法来验证原密码）
                    boolean isOldPasswordCorrect = verifyOldPassword(oldPassword);

                    // 验证新密码是否符合要求
                    boolean isNewPasswordValid = isNewPasswordValid(newPassword);

                    // 验证两次输入的新密码是否一致
                    boolean isConfirmMatch = newPassword.equals(confirmNewPassword);

                    if (isOldPasswordCorrect && isNewPasswordValid && isConfirmMatch) {
                        // 如果所有验证通过，执行修改密码的逻辑
                        changePassword(newPassword);
                        SharedPreferences.Editor editor=getSharedPreferences("myinfo",0).edit();
                        editor.putString("pwd",newPassword);
                        editor.commit();
                        dialog.dismiss(); // 关闭对话框
                    } else {
                        // 如果验证失败，显示相应的错误提示
                        if (!isOldPasswordCorrect) {
                            showToast("原密码错误");
                        }
                        else if (!isNewPasswordValid) {
                            showToast("新密码不符合要求\n"+"至少一个大写字母/小写字母+数字，长度为6-12个密码");
                        }
                        else if (!isConfirmMatch) {
                            showToast("两次输入的新密码不一致");
                        }
                        else if (oldPassword.equals(newPassword)){
                            showToast("新密码不能与旧密码一致");
                        }
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private boolean verifyOldPassword(String oldPassword) {
        if(oldPassword.equals(currentUser.getPassword()))
            return true;
        return false;
    }

    private boolean isNewPasswordValid(String newPassword) {
        // 检查新密码是否符合要求
        return newPassword.length() >= 6 && newPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$");
    }

    private void changePassword(String newPassword) {
        currentUser.setPassword(newPassword);
        api.updateSeeker(currentUser.getPhone(),currentUser).enqueue(new Callback<SeekerDTO>() {
            @Override
            public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                if (response.isSuccessful()) {
                    showToast("密码修改成功");
                    initData();
                } else {
                    showToast("密码修改失败");
                }

            }

            @Override
            public void onFailure(Call<SeekerDTO> call, Throwable t) {
                showToast("网络错误: " + t.getMessage());
            }
        });
    }

    private void showBindDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bind_student, null);
        EditText editGraduationSchool = dialogView.findViewById(R.id.edit_graduation_school);
        EditText editStudentNo = dialogView.findViewById(R.id.edit_student_no);

        new AlertDialog.Builder(this)
                .setTitle("绑定学号")
                .setView(dialogView)
                .setPositiveButton("绑定", (dialog, which) -> {
                    String school = editGraduationSchool.getText().toString().trim();
                    String studentNo = editStudentNo.getText().toString().trim();

                    if (TextUtils.isEmpty(school)) {
                        showToast("毕业院校不能为空");
                        return;
                    }
                    if (TextUtils.isEmpty(studentNo)) {
                        showToast("学号不能为空");
                        return;
                    }

                    performBind(school, studentNo);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showUnbindDialog() {
        new AlertDialog.Builder(this)
                .setTitle("确认解绑")
                .setMessage("确定要解绑当前学号吗？")
                .setPositiveButton("确定", (dialog, which) -> performUnbind())
                .setNegativeButton("取消", null)
                .show();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    private void previewResume() {
        if (TextUtils.isEmpty(currentUser.getResumeFile())) {
            showToast("请先上传简历");
            return;
        }

        try {
            String base64 = currentUser.getResumeFile();
            if (!isValidBase64(base64)) {
                throw new IllegalArgumentException("无效的简历数据");
            }

            byte[] resumeBytes = Base64.decode(base64, Base64.DEFAULT);
            String mimeType = getMimeTypeFromBytes(resumeBytes);

            if (!isSupportedFileType(mimeType)) {
                throw new UnsupportedOperationException("不支持的文件类型");
            }

            showFilePreview(resumeBytes, mimeType);
        } catch (Exception e) {
            Log.e(TAG, "预览简历失败", e);
            showToast("预览失败: " + e.getMessage());
        }
    }

    private void showFilePreview(byte[] fileBytes, String mimeType) {
        if (!mimeType.equals("application/pdf")) {
            Toast.makeText(this, "仅支持 PDF 预览", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. 把字节数组写进缓存文件
        File pdfFile = new File(getCacheDir(), "preview_" + System.currentTimeMillis() + ".pdf");
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            fos.write(fileBytes);
        } catch (IOException e) {
            Toast.makeText(this, "写入缓存失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. 预览对话框
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pdf_preview, null);
        PDFView pdfView = dialogView.findViewById(R.id.pdfView);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("关闭", null)
                .create();

        // 4. 加载 PDF
        pdfView.fromUri(Uri.fromFile(pdfFile))
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onLoad(nbPages -> progressBar.setVisibility(View.GONE))
                .onError(t -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "加载失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .load();

        dialog.show();
    }

    private void cleanOldCacheFiles() {
        new Thread(() -> {
            File cacheDir = new File(getCacheDir(), PREVIEW_CACHE_DIR);
            if (!cacheDir.exists()) return;

            long cutoff = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24小时前
            for (File file : cacheDir.listFiles()) {
                if (file.lastModified() < cutoff) {
                    file.delete();
                }
            }
        }).start();
    }

    private void performBind(String graduationSchool, String studentNo) {
        currentUser.setStudentId(studentNo);
        currentUser.setGraduationSchool(graduationSchool);

        api.updateSeeker(currentUser.getPhone(), currentUser).enqueue(new Callback<SeekerDTO>() {
            @Override
            public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                if (response.isSuccessful()) {
                    showToast("绑定成功");
                    initData();
                } else {
                    showToast("绑定失败");
                }
            }

            @Override
            public void onFailure(Call<SeekerDTO> call, Throwable t) {
                Log.e(TAG, "绑定学号网络错误", t);
                showToast("网络错误: " + t.getMessage());
            }
        });
    }

    private void performUnbind() {
        currentUser.setStudentId("");

        api.updateSeeker(currentUser.getPhone(), currentUser).enqueue(new Callback<SeekerDTO>() {
            @Override
            public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                if (response.isSuccessful()) {
                    showToast("解绑成功");
                    initData();
                } else {
                    showToast("解绑失败");
                }
            }

            @Override
            public void onFailure(Call<SeekerDTO> call, Throwable t) {
                Log.e(TAG, "解绑学号网络错误", t);
                showToast("网络错误: " + t.getMessage());
            }
        });
    }

    private String getMimeTypeFromBytes(byte[] bytes) {
        if (bytes.length >= 4) {
            // PDF: %PDF
            if (bytes[0] == 0x25 && bytes[1] == 0x50 && bytes[2] == 0x44 && bytes[3] == 0x46) {
                return "application/pdf";
            }
            // DOCX: PK
            else if (bytes[0] == 0x50 && bytes[1] == 0x4B) {
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }
            // DOC: D0 CF 11 E0
            else if (bytes[0] == 0xD0 && bytes[1] == 0xCF && bytes[2] == 0x11 && bytes[3] == 0xE0) {
                return "application/msword";
            }
        }
        return "application/octet-stream";
    }

    private boolean isSupportedFileType(String mimeType) {
        return mimeType.startsWith("application/pdf") ||
                mimeType.contains("msword") ||
                mimeType.contains("wordprocessingml");
    }

    private boolean isValidBase64(String str) {
        if (TextUtils.isEmpty(str)) return false;
        try {
            Base64.decode(str, Base64.DEFAULT);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}