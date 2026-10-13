package com.example.smartcampusrecruitment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.model.ExpectJob;

import java.util.ArrayList;
import java.util.List;

public class ExpectAdapter extends RecyclerView.Adapter<ExpectAdapter.ViewHolder> implements Filterable {
    Context mcontext;
    private List<ExpectJob> originalList = new ArrayList<>();
    private List<ExpectJob> displayList;
    public ExpectAdapter(Context mcontext, List<ExpectJob> arr2) {
        this.mcontext = mcontext;
        this.originalList.addAll(arr2);
        this.displayList=arr2;
    }

    @NonNull
    @Override
    public ExpectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.item_expect_job,parent,false);
        ViewHolder mholder=new ViewHolder(view);
        return mholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpectAdapter.ViewHolder holder, int position) {
        ExpectJob expectJob=displayList.get(position);
        holder.item_name.setText(expectJob.getName());
        holder.radio.setChecked(expectJob.isSelected());
        holder.itemView.setOnClickListener(v -> {
            for (ExpectJob e : displayList) e.setSelected(false);
            expectJob.setSelected(true);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ExpectJob> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(originalList);
                } else {
                    String key = constraint.toString().toLowerCase().trim();
                    for (ExpectJob e : originalList) {
                        if (e.getName().toLowerCase().contains(key)) {
                            filtered.add(e);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                displayList.clear();
                displayList.addAll((List<ExpectJob>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_name;
        RadioButton radio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name=itemView.findViewById(R.id.ejName);
            radio=itemView.findViewById(R.id.radio_ej);
        }
    }

    public ExpectJob getSelectedItem() {
        for (ExpectJob item : displayList) {
            if (item.isSelected()) {
                // 需要从原始列表中找到对应的对象
                for (ExpectJob original : originalList) {
                    if (original.getName().equals(item.getName())) {
                        original.setSelected(true);
                        return original;
                    }
                }
                return item;
            }
        }
        return null;
    }
    public void updateData(List<ExpectJob> newData) {
        originalList.clear();
        originalList.addAll(newData);
        displayList.clear();
        displayList.addAll(newData);
        notifyDataSetChanged();
    }
}
