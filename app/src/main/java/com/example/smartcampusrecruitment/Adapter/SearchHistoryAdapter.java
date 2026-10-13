package com.example.smartcampusrecruitment.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.model.SearchJob;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {
    private List<SearchJob> searchJobList;
    private OnItemClickListener listener;

    public SearchHistoryAdapter(List<SearchJob> searchJobList,OnItemClickListener listener) {
        this.searchJobList = searchJobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryAdapter.ViewHolder holder, int position) {
        String name = searchJobList.get(position).getName();
        holder.text_zw_sh.setText(name);
        holder.text_zw_sh.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(name);
        });
    }

    @Override
    public int getItemCount() {
        return searchJobList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String keyword);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_zw_sh;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_zw_sh=itemView.findViewById(R.id.text_zw_sh);
        }
    }
}
