package com.example.smartcampusrecruitment.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.model.dto.JobPositionDTO;

import java.util.ArrayList;
import java.util.List;

public class SearchJobAdapter extends RecyclerView.Adapter<SearchJobAdapter.ViewHolder> {
    Context mcontext;
    private List<JobPositionDTO> jobPositionList;
    private List<JobPositionDTO> filteredList;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(JobPositionDTO job);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SearchJobAdapter(Context mcontext, List<JobPositionDTO> jobPositionList) {
        this.mcontext = mcontext;
        this.jobPositionList = jobPositionList == null ? new ArrayList<>() : jobPositionList;
        this.filteredList = new ArrayList<>(this.jobPositionList);
    }

    @NonNull
    @Override
    public SearchJobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_job, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchJobAdapter.ViewHolder holder, int position) {
        JobPositionDTO currentPosition = filteredList.get(position);
        holder.titleTextView.setText(currentPosition.getTitle());
        holder.salaryTextView.setText(currentPosition.getSalary());
        holder.companyTextView.setText(currentPosition.getCompanyName());
        String recruiter = currentPosition.getRecruiterName() + "·" + currentPosition.getRecruiterPhone();
        holder.zhaopinTextView.setText(recruiter);
        holder.locationTextView.setText(currentPosition.getWorkLocation());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void setFilter(String text, List<String> cityList, String salaryRange) {
        filteredList.clear();
        String kw = text == null ? "" : text.toLowerCase().trim();
        for (JobPositionDTO j : jobPositionList) {
            boolean matchTitle = j.getTitle() != null && j.getTitle().toLowerCase().contains(kw) ||
                    j.getDescription() != null && j.getDescription().toLowerCase().contains(kw);
            boolean matchCity = cityList.isEmpty() ||
                    cityList.stream().anyMatch(c -> j.getWorkLocation() != null &&
                            j.getWorkLocation().toLowerCase().contains(c.toLowerCase()));
            boolean matchSalary = isSalaryInRange(j.getSalary(), salaryRange);

            Log.d("city", j.getWorkLocation());
            if (matchTitle && matchCity && matchSalary) {
                filteredList.add(j);
            }
        }
        notifyDataSetChanged();
    }

    private boolean isSalaryInRange(String jobSalary, String filterRange) {
        if (filterRange == null || filterRange.trim().isEmpty()) return true;

        try {
            String[] range = filterRange.toLowerCase().replace("k", "").split("-");
            int min = Integer.parseInt(range[0]);
            int max = Integer.parseInt(range[1]);

            String[] jobRange = jobSalary.toLowerCase().replace("k", "").split("-");
            int jobMin = Integer.parseInt(jobRange[0]);
            int jobMax = Integer.parseInt(jobRange[1]);

            return jobMax >= min && jobMin <= max; // 有交集即可
        } catch (Exception e) {
            return true; // 解析失败就不限制
        }
    }

    public void updateData(List<JobPositionDTO> jobList) {
        jobPositionList.clear();
        if (jobList != null) jobPositionList.addAll(jobList);
        filteredList.clear();
        filteredList.addAll(jobPositionList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView, salaryTextView, companyTextView, locationTextView, zhaopinTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_job_title);
            salaryTextView = itemView.findViewById(R.id.text_salary);
            companyTextView = itemView.findViewById(R.id.text_company);
            zhaopinTextView = itemView.findViewById(R.id.text_recruiter);
            locationTextView = itemView.findViewById(R.id.text_gzcs);
        }
    }
}
