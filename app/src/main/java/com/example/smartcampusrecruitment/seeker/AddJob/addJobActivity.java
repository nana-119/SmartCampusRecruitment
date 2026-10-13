package com.example.smartcampusrecruitment.seeker.AddJob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.Adapter.JobAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.model.JobItem;
import com.example.smartcampusrecruitment.model.JobWrapper;
import com.example.smartcampusrecruitment.seeker.SeekerMainActivity;
import com.example.smartcampusrecruitment.seeker.sousuoActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class addJobActivity extends AppCompatActivity {
    private TextView text_addjob_aj,text_js;
    private RecyclerView recyclerView;
    private boolean isInitialized = false;
    private ImageView img_fh_aj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_job);
        initView();
        initData();
        Onclick();
        onResume();
    }
    private void initView(){
        text_addjob_aj=findViewById(R.id.text_addjob_aj);
        text_js=findViewById(R.id.text_js);
        recyclerView=findViewById(R.id.recyclerview_aj);
        img_fh_aj=findViewById(R.id.img_fh_aj);
    }
    private void initData(){
        if (isInitialized) return;
        SharedPreferences sp = getSharedPreferences("jobs", MODE_PRIVATE);
        Map<String, ?> all = sp.getAll();
        List<JobWrapper> wrapperList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String[] parts = entry.getValue().toString().split("\\|");
            if (parts.length == 3) {
                JobItem item = new JobItem(parts[0], parts[1], parts[2]);
                wrapperList.add(new JobWrapper(entry.getKey(), item));
            }
        }
        text_js.setText(wrapperList.size()+"/4");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new JobAdapter(wrapperList));
        String js=text_js.getText().toString();
        if ("4/4".equals(js)){
            text_addjob_aj.setVisibility(View.GONE);
        }else {
            text_addjob_aj.setVisibility(View.VISIBLE);
        }
        isInitialized = true;
    }
    private void Onclick(){
        text_addjob_aj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(addJobActivity.this, InsertJobActivity.class);
                startActivity(intent);
            }
        });
        img_fh_aj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(addJobActivity.this, SeekerMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        isInitialized = false;
        initData();
    }
}