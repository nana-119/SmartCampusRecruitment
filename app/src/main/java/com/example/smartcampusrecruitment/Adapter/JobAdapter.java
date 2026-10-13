package com.example.smartcampusrecruitment.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.model.JobItem;
import com.example.smartcampusrecruitment.model.JobWrapper;
import com.example.smartcampusrecruitment.seeker.AddJob.UpdateJobActivity;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobVH>{
    private List<JobWrapper> jobList;
    public void updateData(List<JobWrapper> newData) {
        this.jobList = newData;
        notifyDataSetChanged();
    }

    public JobAdapter(List<JobWrapper> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobAdapter.JobVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_jobclass, parent, false);
        return new JobVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JobAdapter.JobVH holder, int position) {
        JobWrapper w = jobList.get(position);
        JobItem item = w.job;
        holder.jobclass_zw.setText(item.position);
        holder.jobclass_cs.setText(item.cities);
        holder.jobclass_xz.setText(item.salary);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UpdateJobActivity.class); // 目标 Activity
            intent.putExtra("position", item.position);
            intent.putExtra("cities", item.cities);
            intent.putExtra("salary", item.salary);
            intent.putExtra("key", w.key);
            intent.putExtra("node",1);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class JobVH extends RecyclerView.ViewHolder {
        TextView jobclass_zw, jobclass_cs, jobclass_xz;
        public JobVH(@NonNull View itemView) {
            super(itemView);
            jobclass_zw = itemView.findViewById(R.id.item_jobclass_zw);
            jobclass_cs   = itemView.findViewById(R.id.item_jobclass_cs);
            jobclass_xz   = itemView.findViewById(R.id.item_jobclass_xz);
        }
    }
}
