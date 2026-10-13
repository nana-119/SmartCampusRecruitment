package com.example.smartcampusrecruitment.seeker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.Adapter.ContactAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.ContactItem;
import com.example.smartcampusrecruitment.model.dto.ChatMessageDTO;
import com.example.smartcampusrecruitment.model.dto.RecruiterDTO;
import com.example.smartcampusrecruitment.model.dto.SeekerDTO;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class xiaoxiActivity extends AppCompatActivity {
    private ImageView image_zhiwei_xx,image_toudi_xx,image_xiaoxi_xx,image_me_xx;
    private RecyclerView rv_xx;
    private ContactAdapter contactAdapter;
    private List<RecruiterDTO> recruiterDTOList=new ArrayList<>();
    private List<SeekerDTO> seekerDTOList=new ArrayList<>();
    private String firstSenderType = null;
    private final AtomicInteger finished = new AtomicInteger(0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_xiaoxi);
        initView();
        initData();
        onClick();
    }

    private void initView() {
        image_zhiwei_xx=findViewById(R.id.image_zhiwei_xx);
        image_toudi_xx=findViewById(R.id.image_toudi_xx);
        image_xiaoxi_xx=findViewById(R.id.image_xiaoxi_xx);
        image_me_xx=findViewById(R.id.image_me_xx);
        rv_xx=findViewById(R.id.rv_xx);
        rv_xx.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(new ArrayList<>());
        rv_xx.setAdapter(contactAdapter);
    }

    private void onClick() {
        image_zhiwei_xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(xiaoxiActivity.this,SeekerMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        image_toudi_xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(xiaoxiActivity.this,ToudiActivity.class);
                startActivity(intent);
                finish();
            }
        });
        image_xiaoxi_xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        image_me_xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(xiaoxiActivity.this, MeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void refreshUI() {
        if (isRecruiterMode()) {
            contactAdapter.setNewData(ContactAdapter.fromSeekers(seekerDTOList).items);
        } else {
            contactAdapter.setNewData(ContactAdapter.fromRecruiters(recruiterDTOList).items);
        }
    }
    private boolean isRecruiterMode() {
        return "RECRUITER".equals(firstSenderType);
    }

    private void initData() {
        SharedPreferences sp = getSharedPreferences("myinfo", MODE_PRIVATE);
        String phone = sp.getString("phone", "");
        Log.d("phone",phone);
        recruiterDTOList.clear();
        seekerDTOList.clear();
        firstSenderType = null;
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getsenderMessages(phone).enqueue(new Callback<List<ChatMessageDTO>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDTO>> call,
                                   Response<List<ChatMessageDTO>> response) {
                if (!response.isSuccessful() || response.body() == null
                        || response.body().isEmpty()) {
                    Toast.makeText(xiaoxiActivity.this,
                            "没有消息", Toast.LENGTH_LONG).show();
                    return;
                }

                List<ChatMessageDTO> list = response.body();
                Log.d("API", "消息条数：" + list.size());
                // 安全取第一条
                firstSenderType = list.get(0).getSenderType().toString();

                // 去重后的联系人
                Set<String> phoneSet = new LinkedHashSet<>();
                for (ChatMessageDTO dto : list) {
                    phoneSet.add(dto.getReceiverPhone());
                }
                Log.d("API", "联系人电话：" + phoneSet);

                // 并行查询
                if (isRecruiterMode()) {
                    loadSeekers(api, phoneSet);
                } else {
                    loadRecruiters(api, phoneSet);
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessageDTO>> call, Throwable t) {
                Toast.makeText(xiaoxiActivity.this,
                        "网络错误: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        contactAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ContactItem item, int position) {
                Intent intent=new Intent(xiaoxiActivity.this, ChatActivity.class);
                intent.putExtra("CURRENT_USER_PHONE",phone);
                intent.putExtra("OTHER_USER_PHONE",item.getPhone());
                intent.putExtra("IS_RECRUITER",false);
                intent.putExtra("recruiter",item.getName()+"·"+item.getCompany());
                startActivity(intent);
            }
        });
    }

    private void loadSeekers(ApiService api, Set<String> phones) {
        seekerDTOList.clear();
        finished.set(0);
        for (String p : phones) {
            api.SeekerfindbyPhone(p).enqueue(new Callback<SeekerDTO>() {
                @Override
                public void onResponse(Call<SeekerDTO> call, Response<SeekerDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        seekerDTOList.add(response.body());
                        Log.d("API", "SeekerDTO 已添加：" + response.body().getName());
                        if (finished.incrementAndGet() == phones.size()) {
                            refreshUI();
                        }
                    }
                }
                @Override public void onFailure(Call<SeekerDTO> call, Throwable t) {
                    if (finished.incrementAndGet() == phones.size()) {
                        refreshUI();
                    }
                }
            });
        }
    }

    /* 加载所有 Recruiter，统一刷新 */
    private void loadRecruiters(ApiService api, Set<String> phones) {
        recruiterDTOList.clear();
        finished.set(0);
        for (String p : phones) {
            api.RecruiterfindbyPhone(p).enqueue(new Callback<RecruiterDTO>() {
                @Override
                public void onResponse(Call<RecruiterDTO> call, Response<RecruiterDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        recruiterDTOList.add(response.body());
                        Log.d("API", "RecruiterDTO 已添加：" + response.body().getName());
                        if (finished.incrementAndGet() == phones.size()) {
                            refreshUI();
                        }
                    }
                }
                @Override public void onFailure(Call<RecruiterDTO> call, Throwable t) {
                    if (finished.incrementAndGet() == phones.size()) {
                        refreshUI();
                    }
                }
            });
        }
    }

}