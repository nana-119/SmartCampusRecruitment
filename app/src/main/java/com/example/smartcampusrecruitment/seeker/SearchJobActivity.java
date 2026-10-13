package com.example.smartcampusrecruitment.seeker;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.Adapter.SearchJobAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.dto.JobPositionDTO;
import com.example.smartcampusrecruitment.seeker.AddJob.SelectCityActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchJobActivity extends AppCompatActivity {
    private ImageView img_fh_sjd;
    private RecyclerView recyclerView_job_sjd;
    private SearchJobAdapter adapter;
    private List<JobPositionDTO> jobPositionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_job);
        initView();
        initData();
        onClick();
    }

    /* 返回按钮点击事件 */
    private void onClick() {
        img_fh_sjd.setOnClickListener(v -> {
            finish();
        });
    }

    /* 加载职位数据 */
    private void initData() {
        String jobTitle = getIntent().getStringExtra("jobtitle");
        Log.d("jobtitle", jobTitle == null ? "null" : jobTitle);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getJobPosition().enqueue(new Callback<List<JobPositionDTO>>() {
            @Override
            public void onResponse(Call<List<JobPositionDTO>> call, Response<List<JobPositionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<JobPositionDTO> jobList = response.body();
                    adapter.updateData(jobList);
                    adapter.setFilter(jobTitle == null ? "" : jobTitle, new ArrayList<>(), "");
                } else {
                    Toast.makeText(SearchJobActivity.this,
                            "数据获取失败：" + response.code() + " " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<JobPositionDTO>> call, Throwable t) {
                Log.e("API", "网络请求失败", t);
                Toast.makeText(SearchJobActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /* 初始化视图 */
    private void initView() {
        img_fh_sjd = findViewById(R.id.img_fh_sjd);
        recyclerView_job_sjd = findViewById(R.id.recyclerView_job_sjd);
        recyclerView_job_sjd.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SearchJobAdapter(this, jobPositionList);
        recyclerView_job_sjd.setAdapter(adapter);

        adapter.setOnItemClickListener(job -> {
            Toast.makeText(SearchJobActivity.this, "点击了：" + job.getTitle(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SearchJobActivity.this, JobDetailActivity.class);
            intent.putExtra("job", job);   // JobPositionDTO 已实现 Serializable
            startActivity(intent);
        });
    }
}