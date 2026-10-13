package com.example.smartcampusrecruitment.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcampusrecruitment.R;
import com.example.smartcampusrecruitment.model.dto.AcademicRecordDTO;

import java.math.BigDecimal;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {
    private List<AcademicRecordDTO> itemList;

    public ScoreAdapter(List<AcademicRecordDTO> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ScoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreAdapter.ViewHolder holder, int position) {
        AcademicRecordDTO item=itemList.get(position);
        holder.subject.setText(item.getSubjectName());
        holder.score.setText(item.getScore() == null ? "--" : String.valueOf(item.getScore()));
        holder.credit.setText(String.valueOf(item.getCredit()));
        BigDecimal score = item.getScore();
        boolean isPass = score != null && score.compareTo(BigDecimal.valueOf(60.00)) >= 0;
        holder.itemView.setBackgroundColor(isPass ? Color.WHITE : Color.RED);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateData(List<AcademicRecordDTO> displayList) {
        this.itemList=displayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView subject,score,credit;
        public ViewHolder(View itemView) {
            super(itemView);
            subject=itemView.findViewById(R.id.text_subject);
            score=itemView.findViewById(R.id.text_score);
            credit=itemView.findViewById(R.id.text_credit);
        }
    }
}
