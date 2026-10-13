package com.example.smartcampusrecruitment.seeker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.Adapter.MessageAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.Tool.ImageCompressor;
import com.example.smartcampusrecruitment.Tool.MyApplication;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.manager.WebSocketManager;
import com.example.smartcampusrecruitment.model.dto.ChatMessageDTO;
import com.example.smartcampusrecruitment.model.dto.RecruiterDTO;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements WebSocketManager.MessageCallback{
    private ImageView img_fh_chat;
    private TextView text_recruiter_chat;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;
    private List<ChatMessageDTO> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private WebSocketManager webSocketManager;

    private String currentUserPhone;
    private String otherUserPhone;
    private boolean isRecruiter;
    private String recruiter;
    private ApiService api = RetrofitClient.getInstance().create(ApiService.class);
    private String touxiang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Log.e("CRASH", "====== 捕获到未处理异常 ======", e);
            android.os.Process.killProcess(android.os.Process.myPid());
        });
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        currentUserPhone = getIntent().getStringExtra("CURRENT_USER_PHONE");
        otherUserPhone = getIntent().getStringExtra("OTHER_USER_PHONE");
        isRecruiter = getIntent().getBooleanExtra("IS_RECRUITER", false);
        recruiter=getIntent().getStringExtra("recruiter");
        // 初始化WebSocket
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect();
        webSocketManager.subscribeToUser(currentUserPhone, this);


        // 初始化UI
        initViews();
        initData();
        onClick();
    }

    private void initData() {
        text_recruiter_chat.setText(recruiter);
        if (!isRecruiter){
            api.SeekerfindbyPhone(currentUserPhone).enqueue(new Callback<SeekerDTO>() {
                @Override
                public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                    if (response.isSuccessful() && response.body() != null){
                        touxiang=response.body().getTouxiang();
                        touxiang = ImageCompressor.compressBase64(touxiang, 150);
                        Log.d("touxiang",touxiang);
                    }else{
                        Toast.makeText(ChatActivity.this,
                                "应聘者头像获取失败: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<SeekerDTO> call, Throwable t) {
                    Toast.makeText(ChatActivity.this,
                            "网络错误1 " , Toast.LENGTH_LONG).show();
                }
            });
        }else{
            api.RecruiterfindbyPhone(currentUserPhone).enqueue(new Callback<RecruiterDTO>() {
                @Override
                public void onResponse(Call<RecruiterDTO> call, Response<RecruiterDTO> response) {
                    if (response.isSuccessful() && response.body() != null){
                        touxiang=response.body().getTouxiang();
                        touxiang = ImageCompressor.compressBase64(touxiang, 150); // 150px 宽
                    }else{
                        Toast.makeText(ChatActivity.this,
                                "招聘者头像获取失败: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RecruiterDTO> call, Throwable t) {
                    Log.e("NetworkError2", "Recruiter 头像请求失败", t);
                    Toast.makeText(ChatActivity.this,
                            "网络错误2 " , Toast.LENGTH_LONG).show();
                }
            });
        }
        loadHistoryMessages();
        updateAvatars(); // 更新头像
    }

    private void updateAvatars() {
        for (int i = 0; i < messages.size(); i++) {
            ChatMessageDTO message = messages.get(i);
            if (message.getTouxiang() == null || message.getTouxiang().isEmpty()) {
                message.setTouxiang(touxiang); // 更新头像
            }
        }
        adapter.notifyDataSetChanged(); // 通知Adapter数据已更新
    }

    private void onClick() {
        img_fh_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etMessage.getText().toString().trim();
                if (content.isEmpty()) return;

                ChatMessageDTO message = new ChatMessageDTO();
                message.setSenderPhone(currentUserPhone);
                message.setReceiverPhone(otherUserPhone);
                message.setContent(content);
                message.setSenderType(isRecruiter ? ChatMessageDTO.SenderType.RECRUITER : ChatMessageDTO.SenderType.JOB_SEEKER);
                message.setTouxiang(touxiang);
                Log.d("touxiang2",message.getTouxiang());
                webSocketManager.sendMessage(message);
                etMessage.setText("");

                // 本地添加并显示
                messages.add(message);
                adapter.notifyItemInserted(messages.size() - 1);
                scrollToBottom();
            }
        });
    }

    private void loadHistoryMessages() {
        ApiService api= RetrofitClient.getInstance().create(ApiService.class);
        api.getHistoryMessages(currentUserPhone,otherUserPhone).enqueue(new Callback<List<ChatMessageDTO>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDTO>> call, Response<List<ChatMessageDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ChatActivity1", "Response body: " + response.body().toString());
                    Log.d("ChatActivity", "Response body: " + MyApplication.getGson().toJson(response.body()));
                    messages.clear();
                    messages.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    scrollToBottom();
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessageDTO>> call, Throwable t) {
                t.printStackTrace();   // 日志会显示具体错误
                Toast.makeText(ChatActivity.this,"加载历史消息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        img_fh_chat=findViewById(R.id.img_fh_chat);
        text_recruiter_chat=findViewById(R.id.text_recruiter_chat);
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        adapter = new MessageAdapter(messages, currentUserPhone);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);
    }

    @Override
    public void onMessageReceived(ChatMessageDTO message) {
        Log.d("Received Message", MyApplication.getGson().toJson(message));
        runOnUiThread(() -> {
            messages.add(message);
            updateAvatars(); // 更新头像
            adapter.notifyItemInserted(messages.size() - 1);
            scrollToBottom();
        });
    }
    private void scrollToBottom() {
        rvMessages.post(() -> {
            int pos = messages.size() - 1;
            if (pos >= 0) {
                rvMessages.smoothScrollToPosition(pos);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.disconnect();
    }
}