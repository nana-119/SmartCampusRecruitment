package com.example.smartcampusrecruitment.seeker.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {
    private final List<String> titles;
    private final List<String> cities;
    private final List<String> salaries;
    private String biaoshi;
    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity,List<String> titles,List<String> cities,List<String> salaries,String biaoshi) {
        super(fragmentActivity);
        this.titles=titles;
        this.cities=cities;
        this.salaries=salaries;
        this.biaoshi=biaoshi;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if ("Application".equals(biaoshi))
        {
            return toudiFragment.newInstance(titles.get(position));

        }else{
            return jobFragment.newInstance(titles.get(position),cities.get(position),salaries.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
