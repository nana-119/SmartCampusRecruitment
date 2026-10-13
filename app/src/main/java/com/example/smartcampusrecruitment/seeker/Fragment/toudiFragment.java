package com.example.smartcampusrecruitment.seeker.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.Adapter.SearchApplicationAdapter;
import com.example.smartcampusrecruitment.Adapter.SearchJobAdapter;
import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.api.ApiService;
import com.example.smartcampusrecruitment.api.RetrofitClient;
import com.example.smartcampusrecruitment.model.dto.ApplicationDTO;
import com.example.smartcampusrecruitment.model.dto.JobPositionDTO;
import com.example.smartcampusrecruitment.seeker.JobDetailActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class toudiFragment extends Fragment {
    private static final String ARG_DETAIL = "title";
    private RecyclerView recyclerView;
    private SearchApplicationAdapter searchApplicationAdapter;
    private List<ApplicationDTO> applicationList = new ArrayList<>();

    public toudiFragment(){}
    public static toudiFragment newInstance(String title) {
        toudiFragment fragment = new toudiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DETAIL, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toudi, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_job_ftd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchApplicationAdapter = new SearchApplicationAdapter(getContext(), applicationList);
        recyclerView.setAdapter(searchApplicationAdapter);
        searchApplicationAdapter.setOnItemClickListener(new SearchApplicationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ApplicationDTO applicationDTO) {
                Toast.makeText(getContext(), "点击了：" +applicationDTO.getJobTitle(), Toast.LENGTH_SHORT).show();
                Log.d("TOUDI", "jobId=" + applicationDTO.getJobId());
                Intent intent = new Intent(getContext(), JobDetailActivity.class);
                JobPositionDTO job=new JobPositionDTO();
                job.setId(applicationDTO.getJobId());
                job.setTitle(applicationDTO.getJobTitle());
                Log.d("TOUDI", "最终 jobId=" + job.getId());
                intent.putExtra("biaoshi",1);
                intent.putExtra("job", job);   // JobPositionDTO 已 Serializable
                startActivity(intent);
            }
        });
        loadData();
        return view;
    }

    private void loadData() {
        String phone = getContext()
                .getSharedPreferences("myinfo", 0)
                .getString("phone", "");
        String status = getArguments() != null ?
                getArguments().getString(ARG_DETAIL) : "";
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getApplicationByPhone(phone).enqueue(new Callback<List<ApplicationDTO>>() {
            @Override
            public void onResponse(Call<List<ApplicationDTO>> call, Response<List<ApplicationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ApplicationDTO> fullList = response.body();
                    // 根据 status 过滤
                    List<ApplicationDTO> filteredList = new ArrayList<>();
                    if (status == null || status.trim().isEmpty()) {
                        filteredList.addAll(fullList);
                    } else {
                        for (ApplicationDTO dto : fullList) {
                            if (status.equals(dto.getStatus())) {
                                filteredList.add(dto);
                            }
                        }
                    }
                    searchApplicationAdapter.updateData(filteredList);
                } else {
                    Toast.makeText(getContext(),
                            "获取投递记录失败: " + response.code() + " " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ApplicationDTO>> call, Throwable t) {
                Log.e("API", "网络请求失败", t);
                Toast.makeText(getContext(),
                        "网络请求失败: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
