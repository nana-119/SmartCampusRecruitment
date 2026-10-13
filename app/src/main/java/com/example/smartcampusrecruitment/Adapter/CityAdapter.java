package com.example.smartcampusrecruitment.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.model.City;
import com.example.smartcampusrecruitment.R;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityVH>implements Filterable {
    private List<City> cityList;
    private List<City> originalList = new ArrayList<>();
    private OnCitySelectListener listener;

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<City> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(originalList);
                } else {
                    String key = constraint.toString().toLowerCase().trim();
                    for (City c : originalList) {
                        if (c.getName().toLowerCase().contains(key) ||
                                c.getPinyin().toLowerCase().contains(key)) {
                            filtered.add(c);
                        }
                    }
                }
                FilterResults r = new FilterResults();
                r.values = filtered;
                return r;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                cityList.clear();
                if (results.values != null) {
                    cityList.addAll((List<City>) results.values);
                }
                notifyDataSetChanged();
            }
        };

    }

    public interface OnCitySelectListener {
        void onSelect(City city, boolean selected);
    }

    public CityAdapter(List<City> cityList, OnCitySelectListener listener) {
        this.originalList.addAll(cityList);
        this.cityList = new ArrayList<>(cityList);
        this.listener = listener;
    }
    @NonNull
    @Override
    public CityAdapter.CityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);
        return new CityVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAdapter.CityVH holder, int position) {
        City c = cityList.get(position);
        holder.tvName.setText(c.getName());
        holder.check.setChecked(c.isSelected());
        holder.itemView.setOnClickListener(v -> {
            c.setSelected(!c.isSelected());
            notifyItemChanged(position);
            listener.onSelect(c, c.isSelected());
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class CityVH extends RecyclerView.ViewHolder {
        TextView tvName;
        CheckBox check;
        public CityVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCityName);
            check = itemView.findViewById(R.id.cbCity);
        }
    }
    public void updateData(List<City> newData) {
        originalList.clear();
        originalList.addAll(newData);
        cityList.clear();
        cityList.addAll(newData);
        notifyDataSetChanged();
    }
}
