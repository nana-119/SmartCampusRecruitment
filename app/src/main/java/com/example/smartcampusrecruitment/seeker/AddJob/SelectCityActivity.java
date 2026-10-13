package com.example.smartcampusrecruitment.seeker.AddJob;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.Adapter.CityAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.Tool.PinYinUtil;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.City;
import com.example.smartcampusrecruitment.model.dto.JobPositionDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCityActivity extends AppCompatActivity {
    private RecyclerView rvCity;
    private ImageView img_fh_sc;
    private TextView text_yx_sc;
    private Button btn_Save_sc;
    private LinearLayout llSelected;
    private List<City> cityList = new ArrayList<>();
    private List<City> selectedList = new ArrayList<>();
    private CityAdapter adapter;
    private SearchView searchView_cs_sc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_city);
        Log.d("TAG","SelectCityActivity started");
        initView();
        initData();
        adapter=new CityAdapter(cityList, (city, selected) -> {
            if (selected) selectedList.add(city);
            else selectedList.remove(city);
            refreshSelectedTags();
        });
        rvCity.setLayoutManager(new LinearLayoutManager(this));
        rvCity.setAdapter(adapter);
        searchView_cs_sc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        btnSave();
        ImgOnclick();
    }
    private void initView(){
        rvCity=findViewById(R.id.rvCity);
        img_fh_sc=findViewById(R.id.img_fh_sc);
        text_yx_sc=findViewById(R.id.text_yx_sc);
        btn_Save_sc=findViewById(R.id.btn_save_sc);
        llSelected=findViewById(R.id.llSelected);
        searchView_cs_sc=findViewById(R.id.searchView_cs_sc);
    }

    private void initData() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getJobPosition().enqueue(new Callback<List<JobPositionDTO>>() {
            @Override
            public void onResponse(Call<List<JobPositionDTO>> call, Response<List<JobPositionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<JobPositionDTO> jobList = response.body();
                    Set<String> seen = new HashSet<>();
                    List<City> cities = new ArrayList<>();
                    for (JobPositionDTO jp : jobList) {
                        String city = jp.getWorkLocation().split("市")[0];
                        if (seen.add(city)) {
                            cities.add(new City(city, PinYinUtil.getPinyin(city)));
                        }
                    }
                    adapter.updateData(cities);   // 一次性替换
                } else {
                    Toast.makeText(SelectCityActivity.this, "数据获取失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<JobPositionDTO>> call, Throwable t) {
                Toast.makeText(SelectCityActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void ImgOnclick(){
        img_fh_sc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refreshSelectedTags() {
        llSelected.removeAllViews();
        llSelected.addView(text_yx_sc);
        for (City c : selectedList) {
            TextView tag = new TextView(this);
            tag.setText(c.getName() + " ×");
            tag.setPadding(16, 8, 16, 8);
            tag.setBackgroundResource(R.drawable.city_tag_bg);
            tag.setTextColor(Color.WHITE);
            tag.setOnClickListener(v -> {
                c.setSelected(false);
                selectedList.remove(c);
                adapter.notifyDataSetChanged();
                refreshSelectedTags();
            });
            llSelected.addView(tag);
        }
    }
    private void btnSave(){
        btn_Save_sc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> citySet = new HashSet<>();
                for (City c : selectedList) {
                    citySet.add(c.getName());
                }
                String cities = String.join("/", citySet);
                SharedPreferences.Editor editor = getSharedPreferences("jobinfo", MODE_PRIVATE).edit();
                editor.putStringSet("gzcs", citySet);
                editor.apply();
                int node=getIntent().getIntExtra("node",0);
                if (node==1)
                {
                    Intent data = new Intent();
                    data.putExtra("gzcs", cities);
                    setResult(RESULT_OK, data);
                    finish(); // 关闭当前Activity
                }else{
                    Intent intent=new Intent(SelectCityActivity.this, InsertJobActivity.class);
                    intent.putExtra("isDataInitialized",true);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
}