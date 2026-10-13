package com.example.smartcampusrecruitment.seeker.AddJob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smartcampusrecruitment.Adapter.ExpectAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.ExpectJob;
import com.example.smartcampusrecruitment.model.dto.JobPositionDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectJobActivity extends AppCompatActivity {
    private ImageView img_fh_se;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private Button btn_save;
    private List<ExpectJob> ejList=new ArrayList<>();
    private ExpectAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_job);
        initView();
        initData();
        btnSave();
        ImgOnclick();
        adapter=new ExpectAdapter(SelectJobActivity.this,new ArrayList<>());
        StaggeredGridLayoutManager st=new StaggeredGridLayoutManager(StaggeredGridLayoutManager.VERTICAL,1);
        recyclerView.setLayoutManager(st);
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void ImgOnclick() {
        img_fh_se.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        img_fh_se=findViewById(R.id.img_fh_se);
        recyclerView=findViewById(R.id.recyclerview_se);
        btn_save=findViewById(R.id.btn_save_se);
        searchView=findViewById(R.id.searchView_zw_se);
    }
    private void initData(){
        ApiService api= RetrofitClient.getInstance().create(ApiService.class);
        api.getJobPosition().enqueue(new Callback<List<JobPositionDTO>>() {
            @Override
            public void onResponse(Call<List<JobPositionDTO>> call, Response<List<JobPositionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<JobPositionDTO> jobList = response.body();
                    Set<String> seen = new HashSet<>();
                    for (JobPositionDTO jp : jobList) {
                        if (seen.add(jp.getTitle())) {            // 重复标题直接跳过
                            ejList.add(new ExpectJob(jp.getTitle()));
                        }
                    }
                    adapter.updateData(ejList);
                } else {
                    Toast.makeText(SelectJobActivity.this, "数据获取失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<JobPositionDTO>> call, Throwable t) {
                Log.e("API", "网络错误", t);
                Toast.makeText(SelectJobActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
    private void btnSave(){
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpectAdapter adapter = (ExpectAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    ExpectJob selectedJob = adapter.getSelectedItem();
                    if (selectedJob == null) {
                        Toast.makeText(SelectJobActivity.this, "请选择一个职位", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 保存到 SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences("jobinfo", MODE_PRIVATE).edit();
                    editor.putString("qwzw", selectedJob.getName()); // 保存选中的职位名称
                    editor.apply();
                    Toast.makeText(SelectJobActivity.this, "已保存: " + selectedJob.getName(), Toast.LENGTH_SHORT).show();
                    int node=getIntent().getIntExtra("node",0);
                    if (node==1)
                    {
                        Intent data = new Intent();
                        data.putExtra("qwzw", selectedJob.getName());
                        setResult(RESULT_OK, data);
                        finish();   // 直接关闭，回到调用者
//                        Intent intent=new Intent(SelectJobActivity.this, UpdateJobActivity.class);
//                        startActivity(intent);
//                        finish(); // 关闭当前Activity
                    }else {
                        Intent intent=new Intent(SelectJobActivity.this, InsertJobActivity.class);
                        intent.putExtra("isDataInitialized",true);
                        startActivity(intent);
                        finish(); // 关闭当前Activity
                    }

                }
            }
        });
    }
}