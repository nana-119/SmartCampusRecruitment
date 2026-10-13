package com.example.smartcampusrecruitment.seeker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.Adapter.SearchHistoryAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.model.SearchJob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class sousuoActivity extends AppCompatActivity implements SearchHistoryAdapter.OnItemClickListener{

    private Button btn_ss_ss;
    private EditText edit_zw_ss;
    private ImageView img_fh_ss,img_cancle_ss;
    private SearchHistoryAdapter searchHistoryAdapter;
    private RecyclerView recyclerView_ss;
    private List<SearchJob> searchJobList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sousuo);
        initView();
        initData();
        onClick();

    }
    private void initView(){
        btn_ss_ss=findViewById(R.id.btn_ss_ss);
        edit_zw_ss=findViewById(R.id.edit_zw_ss);
        img_fh_ss=findViewById(R.id.img_fh_ss);
        img_cancle_ss=findViewById(R.id.img_cancle_ss);
        recyclerView_ss=findViewById(R.id.recyclerview_ss);
        recyclerView_ss.setLayoutManager(new LinearLayoutManager(this));
        searchHistoryAdapter =new SearchHistoryAdapter(searchJobList,this);
        recyclerView_ss.setAdapter(searchHistoryAdapter);
    }
    private void initData(){
        SharedPreferences sp = getSharedPreferences("search_history", MODE_PRIVATE);
        Set<String> searchHistorySet = sp.getStringSet("search_history", new HashSet<>());
        searchJobList.clear();
        for (String name: searchHistorySet){
            searchJobList.add(new SearchJob(name));
        }
        searchHistoryAdapter.notifyDataSetChanged();
    }
    public void saveSearchHistory(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("search_history", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (name != null) {
            // 从 SharedPreferences 中读取已有的 Set
            Set<String> jobSet = sharedPreferences.getStringSet("search_history", new HashSet<>());
            jobSet.add(name);
            editor.clear().apply();
            editor.putStringSet("search_history", new HashSet<>(jobSet));
            boolean success = editor.commit(); // 使用 commit() 确保数据立即写入文件
            Log.d("SearchHistory", "Commit success: " + success);
        }

    }

    private void onClick(){
        img_fh_ss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_cancle_ss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("search_history", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                searchJobList.clear();
                searchHistoryAdapter.notifyDataSetChanged();
            }
        });
        btn_ss_ss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=edit_zw_ss.getText().toString().trim();
                if (!name.isEmpty()) {
                    saveSearchHistory(name);
                    Intent intent = new Intent(sousuoActivity.this, SearchJobActivity.class);
                    intent.putExtra("jobtitle",name);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onItemClick(String keyword) {
        edit_zw_ss.setText(keyword);
        edit_zw_ss.setSelection(keyword.length());
    }
}