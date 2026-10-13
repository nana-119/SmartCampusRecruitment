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
import com.example.smartcampusrecruitment.model.dto.ApplicationDTO;

import java.util.ArrayList;
import java.util.List;

public class SearchApplicationAdapter extends RecyclerView.Adapter<SearchApplicationAdapter.ViewHolder>{
    Context mcontext;
    private List<ApplicationDTO> originalList;
    private List<ApplicationDTO> filteredList;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(ApplicationDTO applicationDTO);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SearchApplicationAdapter(Context mcontext, List<ApplicationDTO> originalList) {
        this.mcontext = mcontext;
        this.originalList = originalList;
        this.filteredList = new ArrayList<>(this.originalList);
    }

    @NonNull
    @Override
    public SearchApplicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_toudi, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchApplicationAdapter.ViewHolder holder, int position) {
        ApplicationDTO currentPosition = filteredList.get(position);
        holder.titleTextView.setText(currentPosition.getJobTitle());
        holder.companyTextView.setText(currentPosition.getCompanyName());
        holder.applyTextView.setText(currentPosition.getApplicationTime().toString().replace("T"," "));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView, companyTextView, applyTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_job_title_td);
            companyTextView = itemView.findViewById(R.id.text_company_td);
            applyTextView = itemView.findViewById(R.id.text_sqsj_td);
        }
    }
    public void setFilter(String text) {
        filteredList.clear();
        String kw = text == null ? "" : text.toLowerCase().trim();
        for (ApplicationDTO a : originalList) {
            boolean matchStatus = a.getStatus() != null && a.getStatus().toLowerCase().contains(kw);

            Log.d("Status", a.getStatus());
            if (matchStatus ) {
                filteredList.add(a);
            }
        }
        notifyDataSetChanged();
    }
    public void updateData(List<ApplicationDTO> applicationDTOList) {
        originalList.clear();
        if (applicationDTOList != null) originalList.addAll(applicationDTOList);
        filteredList.clear();
        filteredList.addAll(originalList);
        notifyDataSetChanged();
    }
}
