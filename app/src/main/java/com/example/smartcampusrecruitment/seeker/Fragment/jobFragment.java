package com.example.smartcampusrecruitment.seeker.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smartcampusrecruitment.Adapter.SearchJobAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.dto.JobPositionDTO;
import com.example.smartcampusrecruitment.seeker.JobDetailActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class jobFragment extends Fragment {
    private static final String ARG_DETAIL = "title";
    private static final String ARG_CITY   = "city";
    private static final String ARG_SALARY = "salary";

    private RecyclerView recyclerView;
    private SearchJobAdapter searchJobAdapter;
    private List<JobPositionDTO> jobList = new ArrayList<>();

    public jobFragment() { }

    public static jobFragment newInstance(String title, String city, String salary) {
        jobFragment fragment = new jobFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DETAIL, title);
        args.putString(ARG_CITY,   city);
        args.putString(ARG_SALARY, salary);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_job_fj);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchJobAdapter = new SearchJobAdapter(getContext(), jobList);
        recyclerView.setAdapter(searchJobAdapter);

        searchJobAdapter.setOnItemClickListener(job -> {
            Toast.makeText(getContext(), "点击了：" + job.getTitle(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), JobDetailActivity.class);
            intent.putExtra("job", job);   // JobPositionDTO 已 Serializable
            startActivity(intent);
        });

        loadData();
        return view;
    }

    private void loadData() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getJobPosition().enqueue(new Callback<List<JobPositionDTO>>() {
            @Override
            public void onResponse(Call<List<JobPositionDTO>> call,
                                   Response<List<JobPositionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<JobPositionDTO> dtoList = response.body();
                    searchJobAdapter.updateData(dtoList);

                    // 解析过滤参数
                    String title = getArguments() != null ?
                            getArguments().getString(ARG_DETAIL) : null;
                    String cityParam = getArguments() != null ?
                            getArguments().getString(ARG_CITY) : null;
                    String salaryParam = getArguments() != null ?
                            getArguments().getString(ARG_SALARY) : "";

                    if ("综合".equals(title)) title = "";

                    List<String> cityFilterList = new ArrayList<>();
                    if (cityParam != null && !cityParam.isEmpty()) {
                        cityFilterList = Arrays.asList(cityParam.split("\\/"));
                    }

                    searchJobAdapter.setFilter(title, cityFilterList, salaryParam);
                } else {
                    Toast.makeText(getContext(),
                            "数据获取失败: " + response.code() + " " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<JobPositionDTO>> call, Throwable t) {
                Log.e("API", "网络请求失败", t);
                Toast.makeText(getContext(),
                        "网络请求失败: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}